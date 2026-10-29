package com.appw.appwear.shared.model

// Todas las fechas se guardan como milisegundos desde epoch (UTC).
// Los id son UUID porque tanto el teléfono como el reloj crean registros.

data class Mission(
    val id: String,
    val title: String,
    val description: String = "",
    val category: Category,
    val difficulty: Difficulty,
    val recurrence: Recurrence,
    val verification: Verification = Verification.MANUAL,
    /** Minutos para [Verification.TIMER], pasos para [Verification.STEPS]; null para [Verification.MANUAL]. */
    val target: Int? = null,
    val isArchived: Boolean = false,
    val createdAt: Long,
)

/**
 * Registro de una misión completada. La XP y las monedas se guardan al momento de completar,
 * porque dependen de la racha de ese día.
 */
data class Completion(
    val id: String,
    val missionId: String,
    val completedAt: Long,
    val xpEarned: Int,
    val coinsEarned: Int,
    val source: CompletionSource,
    val sensorVerified: Boolean,
)

/** Recompensa definida por el usuario que se canjea con monedas. */
data class Reward(
    val id: String,
    val title: String,
    val cost: Int,
    val isArchived: Boolean = false,
)

/** Canje de una recompensa. Guarda el costo pagado por si la recompensa cambia de precio después. */
data class Redemption(
    val id: String,
    val rewardId: String,
    val redeemedAt: Long,
    val cost: Int,
)
