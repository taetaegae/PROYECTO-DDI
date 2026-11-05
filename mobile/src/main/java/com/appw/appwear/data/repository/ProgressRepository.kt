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

    suspend fun completeMission(
        missionId: String,
        source: CompletionSource,
        sensorVerified: Boolean = false,
    ): CompletionOutcome = completeLock.withLock {
        val mission = missionDao.getById(missionId)?.toDomain()
            ?: return CompletionOutcome.MissionNotFound

        val completions = completionDao.getAll().map { it.toDomain() }
        val today = LocalDate.now(clock)
        val missionDates = completions
            .filter { it.missionId == missionId }
            .map { StreakEngine.localDateOf(it.completedAt, clock.zone) }
        if (MissionSchedule.isCompleted(mission.recurrence, missionDates, today)) {
            return CompletionOutcome.AlreadyCompleted
        }

        // La racha se mide antes de registrar esta misión, como indica XpEngine.xpEarned.
        val streak = StreakEngine.currentStreak(completions, today, clock.zone)
        val xp = XpEngine.xpEarned(mission.difficulty, streak, sensorVerified)
        val coins = XpEngine.coinsFor(xp)
        completionDao.insert(
            Completion(
                id = UUID.randomUUID().toString(),
                missionId = missionId,
                completedAt = clock.millis(),
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
