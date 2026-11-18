package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Completion
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.Verification
import java.time.Instant
import java.time.ZoneId

/**
 * Lo que miden las reglas de las insignias (apartado 5.6 del plan).
 *
 * Como la XP y la racha, se calcula del historial y no se guarda. Por eso la racha que cuenta es
 * la mejor del historial: una insignia ganada no se pierde aunque la racha se rompa después.
 */
data class PlayerStats(
    val completedMissions: Int = 0,
    val bestStreak: Int = 0,
    val level: Int = 1,
    val earlyBirdCompletions: Int = 0,
    val studyTimerMinutes: Int = 0,
    val exerciseCompletions: Int = 0,
    val epicCompletions: Int = 0,
    val redemptions: Int = 0,
) {
    companion object {
        /** Madrugador: antes de esta hora local. */
        const val EARLY_BIRD_HOUR = 8

        /**
         * @param missions todas las misiones por id, también las archivadas.
         *   Un registro cuyo id no está aquí (como los ajustes del modo demo) suma XP, pero no cuenta como misión.
         */
        fun from(
            completions: List<Completion>,
            missions: Map<String, Mission>,
            zone: ZoneId,
            redemptions: Int = 0,
        ): PlayerStats {
            val done = completions.mapNotNull { completion -> missions[completion.missionId]?.let { completion to it } }
            return PlayerStats(
                completedMissions = done.size,
                bestStreak = StreakEngine.bestStreak(
                    done.mapTo(HashSet()) { (completion, _) -> StreakEngine.localDateOf(completion.completedAt, zone) },
                ),
                level = XpEngine.progress(completions.sumOf { it.xpEarned }).level,
                earlyBirdCompletions = done.count { (completion, _) ->
                    Instant.ofEpochMilli(completion.completedAt).atZone(zone).hour < EARLY_BIRD_HOUR
                },
                studyTimerMinutes = done
                    .filter { (completion, mission) ->
                        mission.category == Category.STUDY &&
                            mission.verification == Verification.TIMER &&
                            completion.sensorVerified
                    }
                    .sumOf { (_, mission) -> mission.target ?: 0 },
                exerciseCompletions = done.count { (_, mission) -> mission.category == Category.EXERCISE },
                epicCompletions = done.count { (_, mission) -> mission.difficulty == Difficulty.EPIC },
                redemptions = redemptions,
            )
        }
    }
}

/** Catálogo de insignias del plan. El orden es el de la galería. */
enum class Badge(val rule: (PlayerStats) -> Boolean) {
    FIRST_STEP({ it.completedMissions >= 1 }),
    STREAK_7({ it.bestStreak >= 7 }),
    STREAK_30({ it.bestStreak >= 30 }),
    EARLY_BIRD({ it.earlyBirdCompletions >= 1 }),
    SCHOLAR({ it.studyTimerMinutes >= BadgeEngine.SCHOLAR_MINUTES }),
    ATHLETE({ it.exerciseCompletions >= BadgeEngine.ATHLETE_MISSIONS }),
    EPIC_QUEST({ it.epicCompletions >= 1 }),
    LEVEL_5({ it.level >= 5 }),
    LEVEL_10({ it.level >= 10 }),

    /** Depende de la tienda de recompensas (T6); mientras no exista, queda bloqueada. */
    TREAT_YOURSELF({ it.redemptions >= 1 }),
}

object BadgeEngine {

    /** Erudito: 10 horas de estudio con temporizador. */
    const val SCHOLAR_MINUTES = 600

    /** Atleta: misiones de ejercicio completadas. */
    const val ATHLETE_MISSIONS = 10

    fun unlocked(stats: PlayerStats): Set<Badge> = Badge.entries.filterTo(LinkedHashSet()) { it.rule(stats) }

    fun newlyUnlocked(stats: PlayerStats, unlocked: Set<Badge>): List<Badge> =
        Badge.entries.filter { it !in unlocked && it.rule(stats) }
}
