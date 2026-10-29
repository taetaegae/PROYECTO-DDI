package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Difficulty

/** Posición del jugador dentro de su nivel actual. */
data class LevelProgress(
    val level: Int,
    val xpInLevel: Int,
    val xpForNext: Int,
) {
    /** Avance dentro del nivel, de 0.0 a 1.0. Útil para barras y anillos de progreso. */
    val fraction: Float get() = xpInLevel.toFloat() / xpForNext
}

/** Título que se muestra según el nivel. */
enum class PlayerTitle(val minLevel: Int) {
    NOVICE(1),
    ADVENTURER(5),
    VETERAN(10),
    HERO(15),
    LEGEND(20),
}

/** Reglas de experiencia, niveles y monedas descritas en docs/PLAN_DE_IMPLEMENTACION.md. */
object XpEngine {

    private const val MAX_STREAK_BONUS_TENTHS = 10
    private const val COINS_PER_XP_DIVISOR = 5

    /** XP necesaria para pasar del nivel [level] al siguiente: 100, 200, 300... */
    fun xpToNextLevel(level: Int): Int = 100 * level

    /** XP total acumulada que se necesita para llegar al nivel [level]: 50 × n × (n − 1). */
    fun totalXpForLevel(level: Int): Int = 50 * level * (level - 1)

    fun progress(totalXp: Int): LevelProgress {
        require(totalXp >= 0) { "La XP total no puede ser negativa: $totalXp" }
        var level = 1
        var remaining = totalXp
        while (remaining >= xpToNextLevel(level)) {
            remaining -= xpToNextLevel(level)
            level++
        }
        return LevelProgress(level, xpInLevel = remaining, xpForNext = xpToNextLevel(level))
    }

    /**
     * XP que otorga una misión completada.
     *
     * Fórmula: XP base × (1 + 0.1 × racha, máximo ×2) × (1.25 si la validó un sensor).
     * Se calcula con enteros (décimas y cuartos) para que el redondeo sea exacto.
     *
     * @param streakDays racha vigente antes de registrar esta misión.
     */
    fun xpEarned(difficulty: Difficulty, streakDays: Int, sensorVerified: Boolean): Int {
        val streakTenths = 10 + streakDays.coerceIn(0, MAX_STREAK_BONUS_TENTHS)
        val sensorQuarters = if (sensorVerified) 5 else 4
        val numerator = difficulty.baseXp * streakTenths * sensorQuarters
        // Divide entre 40 (10 décimas × 4 cuartos) redondeando .5 hacia arriba.
        return (numerator + 20) / 40
    }

    fun coinsFor(xp: Int): Int = xp / COINS_PER_XP_DIVISOR

    fun titleFor(level: Int): PlayerTitle =
        PlayerTitle.entries.last { level >= it.minLevel }
}
