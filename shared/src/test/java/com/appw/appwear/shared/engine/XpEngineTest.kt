package com.appw.appwear.shared.engine

import com.appw.appwear.shared.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Test

class XpEngineTest {

    @Test
    fun `xp para subir crece 100 por nivel`() {
        assertEquals(100, XpEngine.xpToNextLevel(1))
        assertEquals(200, XpEngine.xpToNextLevel(2))
        assertEquals(1_000, XpEngine.xpToNextLevel(10))
    }

    @Test
    fun `xp total por nivel coincide con la tabla del plan`() {
        val table = mapOf(1 to 0, 2 to 100, 3 to 300, 5 to 1_000, 10 to 4_500, 15 to 10_500, 20 to 19_000)
        table.forEach { (level, totalXp) ->
            assertEquals("nivel $level", totalXp, XpEngine.totalXpForLevel(level))
        }
    }

    @Test
    fun `progress sin xp empieza en nivel 1`() {
        assertEquals(LevelProgress(level = 1, xpInLevel = 0, xpForNext = 100), XpEngine.progress(0))
    }

    @Test
    fun `progress justo antes y justo en el umbral`() {
        assertEquals(LevelProgress(level = 1, xpInLevel = 99, xpForNext = 100), XpEngine.progress(99))
        assertEquals(LevelProgress(level = 2, xpInLevel = 0, xpForNext = 200), XpEngine.progress(100))
        assertEquals(LevelProgress(level = 9, xpInLevel = 899, xpForNext = 900), XpEngine.progress(4_499))
        assertEquals(LevelProgress(level = 10, xpInLevel = 0, xpForNext = 1_000), XpEngine.progress(4_500))
    }

    @Test
    fun `progress es consistente con totalXpForLevel`() {
        for (level in 1..50) {
            assertEquals(level, XpEngine.progress(XpEngine.totalXpForLevel(level)).level)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `progress rechaza xp negativa`() {
        XpEngine.progress(-1)
    }

    @Test
    fun `fraction refleja el avance dentro del nivel`() {
        // 200 XP = nivel 2 con 100 de 200.
        assertEquals(0.5f, XpEngine.progress(200).fraction, 0.0001f)
    }

    @Test
    fun `sin racha ni sensor se otorga la xp base`() {
        Difficulty.entries.forEach {
            assertEquals(it.baseXp, XpEngine.xpEarned(it, streakDays = 0, sensorVerified = false))
        }
    }

    @Test
    fun `ejemplo del plan - media con racha de 3 y sensor da 41`() {
        assertEquals(41, XpEngine.xpEarned(Difficulty.MEDIUM, streakDays = 3, sensorVerified = true))
    }

    @Test
    fun `guion de la demo - media sin racha con temporizador da 31`() {
        assertEquals(31, XpEngine.xpEarned(Difficulty.MEDIUM, streakDays = 0, sensorVerified = true))
    }

    @Test
    fun `el redondeo de medio punto es hacia arriba`() {
        // 10 × 1.25 = 12.5
        assertEquals(13, XpEngine.xpEarned(Difficulty.EASY, streakDays = 0, sensorVerified = true))
        // 10 × 1.4 × 1.25 = 17.5
        assertEquals(18, XpEngine.xpEarned(Difficulty.EASY, streakDays = 4, sensorVerified = true))
    }

    @Test
    fun `el multiplicador de racha tiene tope de x2`() {
        assertEquals(200, XpEngine.xpEarned(Difficulty.EPIC, streakDays = 10, sensorVerified = false))
        assertEquals(200, XpEngine.xpEarned(Difficulty.EPIC, streakDays = 365, sensorVerified = false))
        assertEquals(250, XpEngine.xpEarned(Difficulty.EPIC, streakDays = 365, sensorVerified = true))
    }

    @Test
    fun `una racha negativa se trata como cero`() {
        assertEquals(25, XpEngine.xpEarned(Difficulty.MEDIUM, streakDays = -3, sensorVerified = false))
    }

    @Test
    fun `las monedas son la xp entre 5 redondeada hacia abajo`() {
        assertEquals(8, XpEngine.coinsFor(41))
        assertEquals(0, XpEngine.coinsFor(4))
        assertEquals(20, XpEngine.coinsFor(100))
    }

    @Test
    fun `titulo segun el nivel`() {
        assertEquals(PlayerTitle.NOVICE, XpEngine.titleFor(1))
        assertEquals(PlayerTitle.NOVICE, XpEngine.titleFor(4))
        assertEquals(PlayerTitle.ADVENTURER, XpEngine.titleFor(5))
        assertEquals(PlayerTitle.VETERAN, XpEngine.titleFor(10))
        assertEquals(PlayerTitle.HERO, XpEngine.titleFor(19))
        assertEquals(PlayerTitle.LEGEND, XpEngine.titleFor(20))
        assertEquals(PlayerTitle.LEGEND, XpEngine.titleFor(99))
    }
}
