package com.appw.appwear.timer

import android.annotation.SuppressLint
import android.content.Context
import com.appw.appwear.LevelUpWearApp
import com.appw.appwear.data.WatchRepository
import com.appw.appwear.feedback.Haptics
import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.Mission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Temporizador activo de una misión TIMER.
 *
 * Se guarda la hora de término ([endsAtMillis]) y no un contador, para que el tiempo restante
 * se pueda recalcular aunque la pantalla se apague o el sistema reinicie la app.
 */
data class TimerState(
    val missionId: String,
    val missionTitle: String,
    val durationMillis: Long,
    /** Hora de término; null mientras está en pausa. */
    val endsAtMillis: Long?,
    /** Lo que faltaba al pausar. Solo se usa en pausa. */
    val pausedRemainingMillis: Long = 0,
) {
    val isPaused: Boolean get() = endsAtMillis == null

    fun remainingMillis(nowMillis: Long): Long =
        if (endsAtMillis == null) pausedRemainingMillis else (endsAtMillis - nowMillis).coerceAtLeast(0)
}

/** Resultado de un temporizador que terminó. [reward] es null si la misión ya estaba completada. */
data class TimerResult(val reward: CompletionReward?)

/**
 * R4 · Lleva la cuenta regresiva y completa la misión al terminar.
 *
 * Mientras corre, [FocusTimerService] mantiene la app en primer plano para que el sistema no la detenga.
 * Hay una sola instancia por app ([get]); la comparten la UI y el servicio.
 */
class FocusTimer private constructor(
    private val context: Context,
    private val repository: WatchRepository,
) {
    // En el hilo principal: así iniciar, pausar y terminar nunca corren al mismo tiempo.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var countdown: Job? = null

    private val _state = MutableStateFlow(restore())
    val state: StateFlow<TimerState?> = _state.asStateFlow()

    private val _result = MutableStateFlow<TimerResult?>(null)

    /** Último temporizador que terminó, hasta que la UI lo muestra y llama a [consumeResult]. */
    val result: StateFlow<TimerResult?> = _result.asStateFlow()

    init {
        // Si el sistema reinició la app con un temporizador corriendo, retoma la cuenta.
        _state.value?.let { if (!it.isPaused) scheduleFinish(it) }
    }

    fun start(mission: Mission, durationMillis: Long) {
        update(
            TimerState(
                missionId = mission.id,
                missionTitle = mission.title,
                durationMillis = durationMillis,
                endsAtMillis = now() + durationMillis,
            ),
        )
    }

    fun pause() {
        val current = _state.value ?: return
        if (current.isPaused) return
        update(current.copy(endsAtMillis = null, pausedRemainingMillis = current.remainingMillis(now())))
    }

    fun resume() {
        val current = _state.value ?: return
        if (!current.isPaused) return
        update(current.copy(endsAtMillis = now() + current.pausedRemainingMillis))
    }

    fun cancel() {
        countdown?.cancel()
        save(null)
        FocusTimerService.stop(context)
    }

    fun consumeResult() {
        _result.value = null
    }

    private fun update(newState: TimerState) {
        save(newState)
        countdown?.cancel()
        if (!newState.isPaused) scheduleFinish(newState)
        // Inicia el servicio o, si ya corre, actualiza su notificación y el wake lock.
        FocusTimerService.start(context)
    }

    private fun scheduleFinish(timer: TimerState) {
        countdown?.cancel()
        countdown = scope.launch {
            delay(timer.remainingMillis(now()))
            finish(timer)
        }
    }

    private suspend fun finish(timer: TimerState) {
        Haptics.timerDone(context)
        // Terminar el temporizador cuenta como verificación del reloj: da 25 % más de XP.
        val reward = repository.completeMission(timer.missionId, sensorVerified = true)
        _result.value = TimerResult(reward)
        save(null)
        FocusTimerService.stop(context)
    }

    private fun save(newState: TimerState?) {
        _state.value = newState
        prefs.edit().apply {
            if (newState == null) {
                clear()
            } else {
                putString(KEY_MISSION_ID, newState.missionId)
                putString(KEY_MISSION_TITLE, newState.missionTitle)
                putLong(KEY_DURATION, newState.durationMillis)
                putLong(KEY_ENDS_AT, newState.endsAtMillis ?: NO_END)
                putLong(KEY_PAUSED_REMAINING, newState.pausedRemainingMillis)
            }
        }.apply()
    }

    private fun restore(): TimerState? {
        val missionId = prefs.getString(KEY_MISSION_ID, null) ?: return null
        val endsAt = prefs.getLong(KEY_ENDS_AT, NO_END)
        return TimerState(
            missionId = missionId,
            missionTitle = prefs.getString(KEY_MISSION_TITLE, null).orEmpty(),
            durationMillis = prefs.getLong(KEY_DURATION, 0),
            endsAtMillis = endsAt.takeIf { it != NO_END },
            pausedRemainingMillis = prefs.getLong(KEY_PAUSED_REMAINING, 0),
        )
    }

    private fun now(): Long = System.currentTimeMillis()

    companion object {
        private const val PREFS_NAME = "focus_timer"
        private const val KEY_MISSION_ID = "mission_id"
        private const val KEY_MISSION_TITLE = "mission_title"
        private const val KEY_DURATION = "duration"
        private const val KEY_ENDS_AT = "ends_at"
        private const val KEY_PAUSED_REMAINING = "paused_remaining"
        private const val NO_END = -1L

        // Guarda el contexto de la Application, no el de una Activity, así que no hay fuga.
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: FocusTimer? = null

        fun get(context: Context): FocusTimer = instance ?: synchronized(this) {
            instance ?: run {
                val app = context.applicationContext as LevelUpWearApp
                FocusTimer(app, app.repository).also { instance = it }
            }
        }
    }
}
