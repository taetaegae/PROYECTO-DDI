package com.appw.appwear.data.repository

import com.appw.appwear.data.db.MissionDao
import com.appw.appwear.data.db.toDomain
import com.appw.appwear.data.db.toEntity
import com.appw.appwear.shared.model.Mission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MissionRepository(private val missionDao: MissionDao) {

    /** Misiones sin archivar, de la más nueva a la más antigua. */
    val activeMissions: Flow<List<Mission>> =
        missionDao.observeActive().map { missions -> missions.map { it.toDomain() } }

    suspend fun getMission(id: String): Mission? = missionDao.getById(id)?.toDomain()

    /** Crea la misión o la reemplaza si ya existe una con el mismo id. */
    suspend fun save(mission: Mission) = missionDao.upsert(mission.toEntity())

    /** Las misiones se archivan en lugar de borrarse para conservar su historial de XP. */
    suspend fun archive(id: String) = missionDao.archive(id)
}
