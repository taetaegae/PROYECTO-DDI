package com.appw.appwear.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionDao {

    @Query("SELECT * FROM completions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completions")
    suspend fun getAll(): List<CompletionEntity>

    /** Modo demo: borra todo el historial para empezar de cero. */
    @Query("DELETE FROM completions")
    suspend fun deleteAll()

    /** Ignora el registro si su id ya existe, para que un evento repetido del reloj no duplique XP. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: CompletionEntity): Long
}
