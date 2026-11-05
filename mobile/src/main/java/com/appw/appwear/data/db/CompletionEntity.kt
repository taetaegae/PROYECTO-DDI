package com.appw.appwear.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.appw.appwear.shared.model.CompletionSource

@Entity(tableName = "completions", indices = [Index("missionId")])
data class CompletionEntity(
    @PrimaryKey val id: String,
    val missionId: String,
    val completedAt: Long,
    val xpEarned: Int,
    val coinsEarned: Int,
    val source: CompletionSource,
    val sensorVerified: Boolean,
)
