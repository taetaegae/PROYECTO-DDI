package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Completion
import com.appw.appwear.shared.model.CompletionSource
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class StreakEngineTest {

    private val today = LocalDate.of(2026, 10, 29)

    private fun daysAgo(vararg offsets: Long): Set<LocalDate> =
        offsets.map { today.minusDays(it) }.toSet()

    @Test
    fun `sin historial la racha es cero`() {
        assertEquals(0, StreakEngine.currentStreak(emptySet(), today))
    }

    @Test
    fun `solo hoy cuenta como racha de 1`() {
        assertEquals(1, StreakEngine.currentStreak(daysAgo(0), today))
    }

    @Test
    fun `si hoy aun no hay misiones la racha de ayer sigue viva`() {
        assertEquals(1, StreakEngine.currentStreak(daysAgo(1), today))
        assertEquals(3, StreakEngine.currentStreak(daysAgo(1, 2, 3), today))
    }

    @Test
    fun `un dia completo sin misiones rompe la racha`() {
        assertEquals(0, StreakEngine.currentStreak(daysAgo(2, 3, 4), today))
    }

    @Test
    fun `cuenta solo los dias consecutivos mas recientes`() {
        assertEquals(3, StreakEngine.currentStreak(daysAgo(0, 1, 2), today))
        assertEquals(1, StreakEngine.currentStreak(daysAgo(0, 2, 3, 4), today))
    }

    @Test
    fun `varias misiones el mismo dia cuentan como un solo dia`() {
        val zone = ZoneOffset.ofHours(-6)
        val completions = listOf(
            completionAt(LocalDateTime.of(2026, 10, 29, 8, 0), zone),
            completionAt(LocalDateTime.of(2026, 10, 29, 20, 0), zone),
        )
        assertEquals(1, StreakEngine.currentStreak(completions, today, zone))
    }

    @Test
    fun `los dias se cuentan en la zona horaria del dispositivo`() {
        // 23:30 y 00:30 hora local son el mismo día en UTC, pero dos días distintos para el jugador.
        val zone = ZoneOffset.ofHours(-6)
        val completions = listOf(
            completionAt(LocalDateTime.of(2026, 10, 28, 23, 30), zone),
            completionAt(LocalDateTime.of(2026, 10, 29, 0, 30), zone),
        )
        assertEquals(2, StreakEngine.currentStreak(completions, today, zone))
        assertEquals(1, StreakEngine.currentStreak(completions, today, ZoneOffset.UTC))
    }

    private fun completionAt(localTime: LocalDateTime, zone: ZoneOffset) = Completion(
        id = localTime.toString(),
        missionId = "mission",
        completedAt = localTime.toInstant(zone).toEpochMilli(),
        xpEarned = 10,
        coinsEarned = 2,
        source = CompletionSource.PHONE,
        sensorVerified = false,
    )
}
