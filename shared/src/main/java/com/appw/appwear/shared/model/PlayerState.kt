package com.appw.appwear.shared.model

import com.appw.appwear.shared.engine.LevelProgress
import com.appw.appwear.shared.engine.PlayerTitle
import com.appw.appwear.shared.engine.XpEngine

/** Resumen del jugador que muestran el dashboard del teléfono y el inicio del reloj. */
data class PlayerProfile(
    val totalXp: Int = 0,
    val streakDays: Int = 0,
    val coins: Int = 0,
) {
    val progress: LevelProgress = XpEngine.progress(totalXp)
    val title: PlayerTitle = XpEngine.titleFor(progress.level)
}

/** Misión que aparece en la lista de hoy junto con su estado en el periodo actual. */
data class TodayMission(
    val mission: Mission,
    val isCompleted: Boolean,
)

/** Lo que ganó el jugador al completar una misión. */
data class CompletionReward(
    val xpEarned: Int,
    val coinsEarned: Int,
    val levelBefore: Int,
    val levelAfter: Int,
) {
    val leveledUp: Boolean get() = levelAfter > levelBefore
}
