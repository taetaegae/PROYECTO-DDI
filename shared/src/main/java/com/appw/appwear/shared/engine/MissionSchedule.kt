package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Recurrence
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Decide si una misión ya se cumplió en su periodo actual a partir de las fechas en que se completó.
 * Como el estado se deduce del historial, no hace falta reiniciar las misiones a medianoche.
 */
object MissionSchedule {

    /** Indica si la misión ya cuenta como cumplida en el periodo que incluye [today]. */
    fun isCompleted(recurrence: Recurrence, completionDates: Collection<LocalDate>, today: LocalDate): Boolean =
        when (recurrence) {
            Recurrence.ONCE -> completionDates.isNotEmpty()
            Recurrence.DAILY -> today in completionDates
            Recurrence.WEEKLY -> {
                val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                completionDates.any { !it.isBefore(weekStart) && !it.isAfter(today) }
            }
        }

    /** Las misiones únicas dejan de aparecer en la lista de hoy a partir del día siguiente de completarse. */
    fun isVisibleOn(recurrence: Recurrence, completionDates: Collection<LocalDate>, today: LocalDate): Boolean =
        recurrence != Recurrence.ONCE || completionDates.none { it.isBefore(today) }
}
