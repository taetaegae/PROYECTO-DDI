package com.appw.appwear.shared.sync

import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.Verification
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncJsonTest {

    @Test
    fun `el perfil sale igual despues de ir y volver como JSON`() {
        val profile = ProfileDto(totalXp = 130, streakDays = 3, coins = 26)

        assertEquals(profile, SyncJson.decodeProfile(SyncJson.encodeProfile(profile)))
    }

    @Test
    fun `las misiones salen iguales despues de ir y volver como JSON`() {
        val missions = listOf(
            MissionDto(
                id = "m1",
                title = "Estudiar 25 min",
                description = "Repasar apuntes",
                category = Category.STUDY,
                difficulty = Difficulty.MEDIUM,
                recurrence = Recurrence.DAILY,
                verification = Verification.TIMER,
                target = 25,
                isCompleted = true,
            ),
        )

        assertEquals(missions, SyncJson.decodeMissions(SyncJson.encodeMissions(missions)))
    }

    @Test
    fun `el evento ignora campos que no conoce`() {
        val text = """{"eventId":"e1","missionId":"m1","completedAt":42,"campoNuevo":true}"""

        val event = SyncJson.decodeEvent(text)

        assertEquals(CompletionEventDto(eventId = "e1", missionId = "m1", completedAt = 42), event)
    }

    @Test
    fun settingsRoundTrip() {
        val settings = SettingsDto(demoMode = true)
        assertEquals(settings, SyncJson.decodeSettings(SyncJson.encodeSettings(settings)))
        assertTrue(SyncJson.decodeSettings(SyncJson.encodeSettings(settings)).demoMode)
    }

    @Test
    fun settingsWithoutFieldsMeansNoDemo() {
        assertFalse(SyncJson.decodeSettings("{}").demoMode)
    }
}
