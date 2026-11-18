package com.appw.appwear.shared.sync

/** Rutas del Data Layer que usan el teléfono y el reloj. Deben ser iguales en las dos apps. */
object SyncPaths {

    /** Teléfono → reloj: XP, racha y monedas del jugador. */
    const val PROFILE = "/levelup/state/profile"

    /** Teléfono → reloj: misiones de hoy con su estado. */
    const val MISSIONS = "/levelup/state/missions"

    /** Teléfono → reloj: ajustes que también aplican en el reloj, como el modo demo. */
    const val SETTINGS = "/levelup/state/settings"

    /** Reloj → teléfono: una misión completada en el reloj. Cada evento lleva su propio id al final. */
    const val COMPLETION_PREFIX = "/levelup/events/completion/"

    fun completion(eventId: String): String = COMPLETION_PREFIX + eventId
}
