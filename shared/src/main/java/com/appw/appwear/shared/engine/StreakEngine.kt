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

    /** La racha más larga del historial: días seguidos con al menos una misión. */
    fun bestStreak(activeDays: Set<LocalDate>): Int {
        var best = 0
        for (day in activeDays) {
            // Se mide solo desde el primer día de cada racha.
            if (day.minusDays(1) in activeDays) continue
            var length = 1
            var next = day.plusDays(1)
            while (next in activeDays) {
                length++
                next = next.plusDays(1)
            }
            best = maxOf(best, length)
        }
        return best
    }

    fun localDateOf(epochMillis: Long, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()
}
