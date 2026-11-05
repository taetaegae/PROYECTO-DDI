package com.appw.appwear.data

import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import kotlinx.coroutines.flow.StateFlow

/** Estado del juego que necesita el reloj. En la Fase 2 lo llenará la sincronización con el teléfono. */
interface WatchRepository {

    val profile: StateFlow<PlayerProfile>

    /** Misiones de hoy; las pendientes primero y las completadas al final. */
    val todayMissions: StateFlow<List<TodayMission>>

    /** Marca la misión como completada. Devuelve null si no existe o ya estaba completada. */
    suspend fun completeMission(missionId: String): CompletionReward?
}
