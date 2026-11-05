package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Recurrence
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MissionScheduleTest {

    // Miércoles 4 de noviembre de 2026; la semana empieza el lunes 2.
    private val today = LocalDate.of(2026, 11, 4)

    @Test
    fun `una misión diaria completada hoy no se puede repetir`() {
        assertTrue(MissionSchedule.isCompleted(Recurrence.DAILY, listOf(today), today))
    }

    @Test
    fun `una misión diaria completada ayer vuelve a estar pendiente`() {
        assertFalse(MissionSchedule.isCompleted(Recurrence.DAILY, listOf(today.minusDays(1)), today))
    }

    @Test
    fun `una misión semanal cuenta desde el lunes`() {
        val monday = LocalDate.of(2026, 11, 2)
        assertTrue(MissionSchedule.isCompleted(Recurrence.WEEKLY, listOf(monday), today))
        assertFalse(MissionSchedule.isCompleted(Recurrence.WEEKLY, listOf(monday.minusDays(1)), today))
    }

    @Test
    fun `una misión única se cumple una sola vez`() {
        assertTrue(MissionSchedule.isCompleted(Recurrence.ONCE, listOf(today.minusDays(10)), today))
        assertFalse(MissionSchedule.isCompleted(Recurrence.ONCE, emptyList(), today))
    }

    @Test
    fun `una misión única completada hoy sigue visible y desaparece mañana`() {
        assertTrue(MissionSchedule.isVisibleOn(Recurrence.ONCE, listOf(today), today))
        assertFalse(MissionSchedule.isVisibleOn(Recurrence.ONCE, listOf(today), today.plusDays(1)))
    }
}
