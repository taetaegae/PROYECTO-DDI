package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Completion
import com.appw.appwear.shared.model.CompletionSource
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.Verification
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class BadgeEngineTest {

    private val zone = ZoneId.of("America/Mexico_City")

    private fun mission(
        id: String,
        category: Category = Category.HABITS,
        difficulty: Difficulty = Difficulty.EASY,
        verification: Verification = Verification.MANUAL,
        target: Int? = null,
    ) = Mission(id, id, category = category, difficulty = difficulty, recurrence = Recurrence.DAILY,
        verification = verification, target = target, createdAt = 0)

    private fun completion(
        missionId: String,
        at: LocalDateTime = LocalDateTime.of(2026, 11, 17, 12, 0),
        xp: Int = 10,
        sensorVerified: Boolean = false,
    ) = Completion("$missionId-$at", missionId, at.atZone(zone).toInstant().toEpochMilli(), xp, xp / 5,
        CompletionSource.PHONE, sensorVerified)

    private fun badgesFor(completions: List<Completion>, vararg missions: Mission): Set<Badge> =
        BadgeEngine.unlocked(PlayerStats.from(completions, missions.associateBy { it.id }, zone))

    @Test
    fun withoutHistoryNoBadgeIsUnlocked() {
        assertEquals(emptySet<Badge>(), badgesFor(emptyList()))
    }

    @Test
    fun firstStepNeedsOneCompletedMission() {
        assertTrue(Badge.FIRST_STEP in badgesFor(listOf(completion("leer")), mission("leer")))
    }

    @Test
    fun demoAdjustmentAddsXpButIsNotAMission() {
        // Registro de ajuste del modo demo: su misión no existe.
        val adjustment = completion("demo-ajuste", xp = 1_000)
        val badges = badgesFor(listOf(adjustment))
        assertFalse(Badge.FIRST_STEP in badges)
        assertTrue(Badge.LEVEL_5 in badges)
    }

    @Test
    fun streakBadgesUseTheBestStreak() {
        assertFalse(Badge.STREAK_7.rule(PlayerStats(bestStreak = 6)))
        assertTrue(Badge.STREAK_7.rule(PlayerStats(bestStreak = 7)))
        assertFalse(Badge.STREAK_30.rule(PlayerStats(bestStreak = 29)))
        assertTrue(Badge.STREAK_30.rule(PlayerStats(bestStreak = 30)))
    }

    @Test
    fun brokenStreakKeepsTheBadge() {
        // Siete días seguidos en octubre y después un día suelto: la mejor racha sigue siendo 7.
        val week = (1..7).map { completion("leer", LocalDateTime.of(2026, 10, it, 12, 0)) }
        val later = completion("leer", LocalDateTime.of(2026, 11, 17, 12, 0))
        assertTrue(Badge.STREAK_7 in badgesFor(week + later, mission("leer")))
    }

    @Test
    fun bestStreakFindsTheLongestRun() {
        val days = setOf(
            LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 6), LocalDate.of(2026, 11, 7),
        )
        assertEquals(3, StreakEngine.bestStreak(days))
        assertEquals(0, StreakEngine.bestStreak(emptySet()))
    }

    @Test
    fun earlyBirdIsBeforeEightLocalTime() {
        val m = mission("correr")
        assertTrue(Badge.EARLY_BIRD in badgesFor(listOf(completion("correr", LocalDateTime.of(2026, 11, 17, 7, 59))), m))
        assertFalse(Badge.EARLY_BIRD in badgesFor(listOf(completion("correr", LocalDateTime.of(2026, 11, 17, 8, 0))), m))
    }

    @Test
    fun scholarCountsOnlyStudyMinutesFinishedWithTheTimer() {
        val study = mission("estudiar", Category.STUDY, Difficulty.MEDIUM, Verification.TIMER, target = 25)
        // 24 sesiones de 25 minutos son las 10 horas.
        val sessions = (1..24).map { completion("estudiar", LocalDateTime.of(2026, 11, 17, 9, 0).plusMinutes(it.toLong()), sensorVerified = true) }
        assertFalse(Badge.SCHOLAR in badgesFor(sessions.drop(1), study))
        assertTrue(Badge.SCHOLAR in badgesFor(sessions, study))
        // Marcadas a mano no cuentan.
        val manual = sessions.map { it.copy(sensorVerified = false) }
        assertFalse(Badge.SCHOLAR in badgesFor(manual, study))
    }

    @Test
    fun athleteNeedsTenExerciseMissions() {
        val run = mission("correr", Category.EXERCISE)
        val runs = (1..10).map { completion("correr", LocalDateTime.of(2026, 11, it, 12, 0)) }
        assertFalse(Badge.ATHLETE in badgesFor(runs.drop(1), run))
        assertTrue(Badge.ATHLETE in badgesFor(runs, run))
    }

    @Test
    fun epicQuestNeedsAnEpicMission() {
        val epic = mission("proyecto", difficulty = Difficulty.EPIC)
        assertTrue(Badge.EPIC_QUEST in badgesFor(listOf(completion("proyecto", xp = 100)), epic))
        assertFalse(Badge.EPIC_QUEST in badgesFor(listOf(completion("leer")), mission("leer")))
    }

    @Test
    fun levelBadges() {
        assertFalse(Badge.LEVEL_5.rule(PlayerStats(level = 4)))
        assertTrue(Badge.LEVEL_5.rule(PlayerStats(level = 5)))
        assertFalse(Badge.LEVEL_10.rule(PlayerStats(level = 9)))
        assertTrue(Badge.LEVEL_10.rule(PlayerStats(level = 10)))
    }

    @Test
    fun treatYourselfNeedsARedemption() {
        assertFalse(Badge.TREAT_YOURSELF.rule(PlayerStats()))
        assertTrue(Badge.TREAT_YOURSELF.rule(PlayerStats(redemptions = 1)))
    }

    @Test
    fun newlyUnlockedSkipsBadgesAlreadyUnlocked() {
        val stats = PlayerStats(completedMissions = 1, level = 5)
        assertEquals(listOf(Badge.LEVEL_5), BadgeEngine.newlyUnlocked(stats, setOf(Badge.FIRST_STEP)))
    }
}
