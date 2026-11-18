package com.appw.appwear.data.repository

import com.appw.appwear.data.db.CompletionDao
import com.appw.appwear.data.db.MissionDao
import com.appw.appwear.data.db.toDomain
import com.appw.appwear.data.db.toEntity
import com.appw.appwear.shared.engine.MissionSchedule
import com.appw.appwear.shared.engine.StreakEngine
import com.appw.appwear.shared.engine.XpEngine
import com.appw.appwear.shared.model.Completion
import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.CompletionSource
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Clock
import java.time.LocalDate
import java.util.UUID

/** Resultado de intentar completar una misión. */
sealed interface CompletionOutcome {
    data class Completed(val reward: CompletionReward) : CompletionOutcome
    data object AlreadyCompleted : CompletionOutcome
    data object MissionNotFound : CompletionOutcome
}

/**
 * Calcula el progreso del jugador a partir del historial de misiones completadas.
 * La XP, el nivel, las monedas y la racha no se guardan: se derivan de los registros.
 */
class ProgressRepository(
    private val missionDao: MissionDao,
    private val completionDao: CompletionDao,
    private val clock: Clock,
) {
    // Evita que dos toques seguidos registren la misma misión dos veces.
    private val completeLock = Mutex()

    val profile: Flow<PlayerProfile> = completionDao.observeAll().map { entities ->
        val completions = entities.map { it.toDomain() }
        PlayerProfile(
            totalXp = completions.sumOf { it.xpEarned },
            streakDays = StreakEngine.currentStreak(completions, LocalDate.now(clock), clock.zone),
            coins = completions.sumOf { it.coinsEarned },
        )
    }

    /** Misiones de hoy; las pendientes primero y las completadas al final. */
    val todayMissions: Flow<List<TodayMission>> =
        combine(missionDao.observeActive(), completionDao.observeAll()) { missions, completions ->
            val today = LocalDate.now(clock)
            val datesByMission = completions.groupBy(
                keySelector = { it.missionId },
                valueTransform = { StreakEngine.localDateOf(it.completedAt, clock.zone) },
            )
            missions
                .map { it.toDomain() }
                .mapNotNull { mission ->
                    val dates = datesByMission[mission.id].orEmpty()
                    if (MissionSchedule.isVisibleOn(mission.recurrence, dates, today)) {
                        TodayMission(mission, MissionSchedule.isCompleted(mission.recurrence, dates, today))
                    } else {
                        null
                    }
                }
                .sortedBy { it.isCompleted }
        }

    /**
     * Modo demo: deja al jugador a [DEMO_XP_BEFORE_LEVEL_UP] XP del siguiente nivel.
     *
     * Como la XP se deriva del historial, agrega un registro de ajuste fechado en 1970 y con un
     * id de misión que no existe; así no cambia la racha ni las misiones de hoy.
     *
     * @return el nivel al que subirá el jugador con la siguiente misión.
     */
    suspend fun prepareDemoLevelUp(): Int = completeLock.withLock {
        val totalXp = completionDao.getAll().sumOf { it.xpEarned }
        var nextLevel = XpEngine.progress(totalXp).level + 1
        // Si ya le falta menos que eso, se apunta al nivel siguiente.
        if (XpEngine.totalXpForLevel(nextLevel) - totalXp < DEMO_XP_BEFORE_LEVEL_UP) nextLevel++
        val adjustment = XpEngine.totalXpForLevel(nextLevel) - DEMO_XP_BEFORE_LEVEL_UP - totalXp
        if (adjustment > 0) {
            completionDao.insert(
                Completion(
                    id = UUID.randomUUID().toString(),
                    missionId = DEMO_ADJUSTMENT_MISSION_ID,
                    completedAt = 0,
                    xpEarned = adjustment,
                    coinsEarned = 0,
                    source = CompletionSource.PHONE,
                    sensorVerified = false,
                ).toEntity(),
            )
        }
        nextLevel
    }

    suspend fun completeMission(
        missionId: String,
        source: CompletionSource,
        sensorVerified: Boolean = false,
        completionId: String = UUID.randomUUID().toString(),
        completedAt: Long = clock.millis(),
    ): CompletionOutcome = completeLock.withLock {
        val mission = missionDao.getById(missionId)?.toDomain()
            ?: return CompletionOutcome.MissionNotFound

        val completions = completionDao.getAll().map { it.toDomain() }
        val day = StreakEngine.localDateOf(completedAt, clock.zone)
        val missionDates = completions
            .filter { it.missionId == missionId }
            .map { StreakEngine.localDateOf(it.completedAt, clock.zone) }
        if (MissionSchedule.isCompleted(mission.recurrence, missionDates, day)) {
            return CompletionOutcome.AlreadyCompleted
        }

        // La racha se mide antes de registrar esta misión, como indica XpEngine.xpEarned.
        val streak = StreakEngine.currentStreak(completions, day, clock.zone)
        val xp = XpEngine.xpEarned(mission.difficulty, streak, sensorVerified)
        val coins = XpEngine.coinsFor(xp)
        completionDao.insert(
            Completion(
                id = completionId,
                missionId = missionId,
                completedAt = completedAt,
                xpEarned = xp,
                coinsEarned = coins,
                source = source,
                sensorVerified = sensorVerified,
            ).toEntity(),
        )

        val totalXpBefore = completions.sumOf { it.xpEarned }
        CompletionOutcome.Completed(
            CompletionReward(
                xpEarned = xp,
                coinsEarned = coins,
                levelBefore = XpEngine.progress(totalXpBefore).level,
                levelAfter = XpEngine.progress(totalXpBefore + xp).level,
            ),
        )
    }
}

/** XP que le falta al jugador para subir de nivel después de preparar la demo. */
const val DEMO_XP_BEFORE_LEVEL_UP = 10

/** Id de misión de los registros de ajuste del modo demo. No corresponde a ninguna misión real. */
const val DEMO_ADJUSTMENT_MISSION_ID = "demo-ajuste"
