package com.appw.appwear.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.Verification

// Room guarda los enums como texto con su nombre, así que no hacen falta TypeConverters.
@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val difficulty: Difficulty,
    val recurrence: Recurrence,
    val verification: Verification,
    val target: Int?,
    val isArchived: Boolean,
    val createdAt: Long,
)
