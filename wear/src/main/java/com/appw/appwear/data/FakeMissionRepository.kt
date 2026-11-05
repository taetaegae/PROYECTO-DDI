package com.appw.appwear.data

import com.appw.appwear.shared.engine.XpEngine
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.shared.model.Verification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Datos de ejemplo en memoria para construir las pantallas del reloj antes de la sincronización. */
class FakeMissionRepository : WatchRepository {

    // 80 XP: la primera misión completada sube al jugador al nivel 2.
    private val _profile = MutableStateFlow(PlayerProfile(totalXp = 80, streakDays = 2, coins = 16))
    override val profile: StateFlow<PlayerProfile> = _profile.asStateFlow()

    private val _todayMissions = MutableStateFlow(sampleMissions.map { TodayMission(it, isCompleted = false) })
    override val todayMissions: StateFlow<List<TodayMission>> = _todayMissions.asStateFlow()

    override suspend fun completeMission(missionId: String): CompletionReward? {
        val item = _todayMissions.value.find { it.mission.id == missionId }
        if (item == null || item.isCompleted) return null

        val before = _profile.value
        val xp = XpEngine.xpEarned(item.mission.difficulty, before.streakDays, sensorVerified = false)
        val coins = XpEngine.coinsFor(xp)
        val after = before.copy(totalXp = before.totalXp + xp, coins = before.coins + coins)

        _profile.value = after
        _todayMissions.value = _todayMissions.value
            .map { if (it.mission.id == missionId) it.copy(isCompleted = true) else it }
            .sortedBy { it.isCompleted }

        return CompletionReward(
            xpEarned = xp,
            coinsEarned = coins,
            levelBefore = before.progress.level,
            levelAfter = after.progress.level,
        )
    }

    companion object {
        val sampleMissions = listOf(
            Mission(
                id = "fake-study",
                title = "Estudiar 25 min",
                description = "Repasar los apuntes de la clase.",
                category = Category.STUDY,
                difficulty = Difficulty.MEDIUM,
                recurrence = Recurrence.DAILY,
                verification = Verification.TIMER,
                target = 25,
                createdAt = 0,
            ),
            Mission(
                id = "fake-walk",
                title = "Caminar 20 min",
                category = Category.EXERCISE,
                difficulty = Difficulty.EASY,
                recurrence = Recurrence.DAILY,
                createdAt = 0,
            ),
            Mission(
                id = "fake-read",
                title = "Leer 10 páginas",
                category = Category.HABITS,
                difficulty = Difficulty.EASY,
                recurrence = Recurrence.DAILY,
                createdAt = 0,
            ),
            Mission(
                id = "fake-draw",
                title = "Dibujar un boceto",
                category = Category.CREATIVE,
                difficulty = Difficulty.HARD,
                recurrence = Recurrence.WEEKLY,
                createdAt = 0,
            ),
        )
    }
}
