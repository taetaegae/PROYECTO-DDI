package com.appw.appwear.shared.model

/** Dificultad de una misión y la XP base que otorga. */
enum class Difficulty(val baseXp: Int) {
    EASY(10),
    MEDIUM(25),
    HARD(50),
    EPIC(100),
}

/** Categoría de una misión. Cada una alimenta un atributo del jugador. */
enum class Category {
    STUDY,
    EXERCISE,
    HABITS,
    CREATIVE,
}

/** Cada cuánto se repite una misión. */
enum class Recurrence {
    ONCE,
    DAILY,
    WEEKLY,
}

/** Cómo se comprueba que una misión se cumplió. */
enum class Verification {
    /** El usuario la marca como completada. */
    MANUAL,

    /** Se completa al terminar el temporizador del reloj. */
    TIMER,

    /** Se completa al alcanzar la meta de pasos. */
    STEPS,
}

/** Dispositivo u origen que registró una misión completada. */
enum class CompletionSource {
    PHONE,
    WATCH,

    /** Generada automáticamente por un sensor del reloj. */
    AUTO,
}
