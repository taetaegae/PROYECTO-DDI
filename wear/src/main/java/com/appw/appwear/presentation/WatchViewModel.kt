package com.appw.appwear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.LevelUpWearApp
import com.appw.appwear.data.WatchRepository
import com.appw.appwear.presentation.missions.DEFAULT_TIMER_MINUTES
import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.timer.FocusTimer
import com.appw.appwear.timer.TimerResult
import com.appw.appwear.timer.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado compartido por todas las pantallas del reloj. */
class WatchViewModel(
    private val repository: WatchRepository,
    private val focusTimer: FocusTimer,
) : ViewModel() {

    val profile: StateFlow<PlayerProfile> = repository.profile
    val todayMissions: StateFlow<List<TodayMission>> = repository.todayMissions

    /** Recompensa de la última misión completada, para la pantalla R5. */
    private val _lastReward = MutableStateFlow<CompletionReward?>(null)
    val lastReward: StateFlow<CompletionReward?> = _lastReward.asStateFlow()

    fun completeMission(missionId: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            val reward = repository.completeMission(missionId) ?: return@launch
            _lastReward.value = reward
            onCompleted()
        }
    }

    /** Temporizador activo (R4), o null si no hay ninguno. */
    val timer: StateFlow<TimerState?> = focusTimer.state

    /** Temporizador que acaba de terminar, pendiente de mostrarse en R5. */
    val timerResult: StateFlow<TimerResult?> = focusTimer.result

    fun startTimer(mission: Mission) {
        val minutes = mission.target ?: DEFAULT_TIMER_MINUTES
        focusTimer.start(mission, minutes * 60_000L)
    }

    fun pauseTimer() = focusTimer.pause()

    fun resumeTimer() = focusTimer.resume()

    fun cancelTimer() = focusTimer.cancel()

    /** Pasa la recompensa del temporizador a R5 y lo marca como mostrado. */
    fun acknowledgeTimerResult(result: TimerResult) {
        if (result.reward != null) _lastReward.value = result.reward
        focusTimer.consumeResult()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as LevelUpWearApp
                WatchViewModel(app.repository, FocusTimer.get(app))
            }
        }
    }
}
