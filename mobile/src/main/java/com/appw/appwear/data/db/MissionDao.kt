package com.appw.appwear.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {

    @Query("SELECT * FROM missions WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<MissionEntity>>

    /** Todas las misiones, también las archivadas: las insignias cuentan su historial. */
    @Query("SELECT * FROM missions")
    fun observeAll(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions")
    suspend fun getAll(): List<MissionEntity>

    @Query("SELECT * FROM missions WHERE id = :id")
    suspend fun getById(id: String): MissionEntity?

    @Upsert
    suspend fun upsert(mission: MissionEntity)

    @Query("UPDATE missions SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: String)
}
