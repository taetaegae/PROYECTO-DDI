package com.appw.appwear.shared.sync

import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.shared.model.Verification
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Datos que viajan entre el teléfono y el reloj como texto JSON.

/** Progreso del jugador. El nivel y el título se calculan con XpEngine a partir de la XP total. */
@Serializable
data class ProfileDto(
    val totalXp: Int,
    val streakDays: Int,
    val coins: Int,
)

/** Una misión de hoy y si ya se completó en el periodo actual. */
@Serializable
data class MissionDto(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val difficulty: Difficulty,
    val recurrence: Recurrence,
    val verification: Verification,
    val target: Int?,
    val isCompleted: Boolean,
)

/** Aviso del reloj al teléfono: el jugador completó una misión. */
@Serializable
data class CompletionEventDto(
    /** Id único del evento. El teléfono lo usa como id del registro para no contarlo dos veces. */
    val eventId: String,
    val missionId: String,
    val completedAt: Long,
    val sensorVerified: Boolean = false,
    /** Valor medido por un sensor (minutos o pasos). Se usará en la Fase 4. */
    val measuredValue: Int? = null,
)

fun PlayerProfile.toDto(): ProfileDto = ProfileDto(totalXp, streakDays, coins)

fun ProfileDto.toProfile(): PlayerProfile = PlayerProfile(totalXp, streakDays, coins)

fun TodayMission.toDto(): MissionDto = MissionDto(
    id = mission.id,
    title = mission.title,
    description = mission.description,
    category = mission.category,
    difficulty = mission.difficulty,
    recurrence = mission.recurrence,
    verification = mission.verification,
    target = mission.target,
    isCompleted = isCompleted,
)

fun MissionDto.toTodayMission(): TodayMission = TodayMission(
    mission = Mission(
        id = id,
        title = title,
        description = description,
        category = category,
        difficulty = difficulty,
        recurrence = recurrence,
        verification = verification,
        target = target,
        createdAt = 0,
    ),
    isCompleted = isCompleted,
)

/** Convierte los DTO a texto JSON y de regreso. */
object SyncJson {

    // ignoreUnknownKeys permite agregar campos nuevos sin romper una versión anterior de la otra app.
    private val json = Json { ignoreUnknownKeys = true }

    fun encodeProfile(profile: ProfileDto): String = json.encodeToString(profile)

    fun decodeProfile(text: String): ProfileDto = json.decodeFromString(text)

    fun encodeMissions(missions: List<MissionDto>): String = json.encodeToString(missions)

    fun decodeMissions(text: String): List<MissionDto> = json.decodeFromString(text)

    fun encodeEvent(event: CompletionEventDto): String = json.encodeToString(event)

    fun decodeEvent(text: String): CompletionEventDto = json.decodeFromString(text)
}
