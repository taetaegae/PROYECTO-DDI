package com.appw.appwear.presentation.navigation

/** Rutas del reloj (R1 a R6 en el plan de implementación). */
object WearDestination {
    const val HOME = "home"
    const val MISSIONS = "missions"
    const val ARG_MISSION_ID = "missionId"
    const val MISSION_DETAIL = "missions/{missionId}"
    const val MISSION_DONE = "mission_done"

    fun missionDetail(missionId: String): String = "missions/$missionId"
}
