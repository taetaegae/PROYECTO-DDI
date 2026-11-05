package com.appw.appwear.data.db

import com.appw.appwear.shared.model.Completion
import com.appw.appwear.shared.model.Mission

fun MissionEntity.toDomain() = Mission(
    id = id,
    title = title,
    description = description,
    category = category,
    difficulty = difficulty,
    recurrence = recurrence,
    verification = verification,
    target = target,
    isArchived = isArchived,
    createdAt = createdAt,
)

fun Mission.toEntity() = MissionEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    difficulty = difficulty,
    recurrence = recurrence,
    verification = verification,
    target = target,
    isArchived = isArchived,
    createdAt = createdAt,
)

fun CompletionEntity.toDomain() = Completion(
    id = id,
    missionId = missionId,
    completedAt = completedAt,
    xpEarned = xpEarned,
    coinsEarned = coinsEarned,
    source = source,
    sensorVerified = sensorVerified,
)

fun Completion.toEntity() = CompletionEntity(
    id = id,
    missionId = missionId,
    completedAt = completedAt,
    xpEarned = xpEarned,
    coinsEarned = coinsEarned,
    source = source,
    sensorVerified = sensorVerified,
)
