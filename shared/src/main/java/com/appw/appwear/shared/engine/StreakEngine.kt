package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Completion
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Calcula la racha a partir del historial. Un día cuenta si se completó al menos una misión,
 * y la racha vigente son los días consecutivos que terminan hoy o ayer.
 */
object StreakEngine {

    fun currentStreak(activeDays: Set<LocalDate>, today: LocalDate): Int {
        // Si hoy todavía no hay misiones, la racha de ayer sigue viva.
        var day = if (today in activeDays) today else today.minusDays(1)
        var streak = 0
        while (day in activeDays) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }

    fun currentStreak(completions: Iterable<Completion>, today: LocalDate, zone: ZoneId): Int =
        currentStreak(completions.mapTo(HashSet()) { localDateOf(it.completedAt, zone) }, today)

    fun localDateOf(epochMillis: Long, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()
}
