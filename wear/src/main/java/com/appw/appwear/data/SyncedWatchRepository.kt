package com.appw.appwear.data

import com.appw.appwear.shared.engine.XpEngine
import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.sync.EventSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SyncedWatchRepository(
    private val cache: WatchStateCache,
    private val eventSender: EventSender,
    scope: CoroutineScope,
) : WatchRepository {
    override val profile: StateFlow<PlayerProfile> = cache.profile.stateIn(scope, SharingStarted.Eagerly, PlayerProfile())
    override val todayMissions: StateFlow<List<TodayMission>> = cache.todayMissions.stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun completeMission(missionId: String): CompletionReward? {
        val item = todayMissions.value.find { it.mission.id == missionId }
        if (item == null || item.isCompleted) return null
        val before = profile.value
        val xp = XpEngine.xpEarned(item.mission.difficulty, before.streakDays, sensorVerified = false)
        val coins = XpEngine.coinsFor(xp)
        val after = before.copy(totalXp = before.totalXp + xp, coins = before.coins + coins)
        val missions = todayMissions.value.map { if (it.mission.id == missionId) it.copy(isCompleted = true) else it }.sortedBy { it.isCompleted }
        cache.saveLocal(after, missions)
        eventSender.sendCompletion(missionId)
        return CompletionReward(xp, coins, before.progress.level, after.progress.level)
    }
}
