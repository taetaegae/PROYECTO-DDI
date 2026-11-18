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

    /** Modo demo publicado por el teléfono: el temporizador dura 10 segundos. */
    val demoMode: StateFlow<Boolean>

    /**
     * Marca la misión como completada. Devuelve null si no existe o ya estaba completada.
     *
     * @param sensorVerified true si la validó el temporizador; da 25 % más de XP.
     */
    suspend fun completeMission(missionId: String, sensorVerified: Boolean = false): CompletionReward?
}
