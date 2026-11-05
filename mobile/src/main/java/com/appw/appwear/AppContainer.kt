package com.appw.appwear

import android.content.Context
import com.appw.appwear.data.db.AppDatabase
import com.appw.appwear.data.repository.MissionRepository
import com.appw.appwear.data.repository.ProgressRepository
import java.time.Clock

/** Dependencias de la app. Se crean una sola vez en [LevelUpApp]. */
class AppContainer(context: Context) {

    private val database = AppDatabase.build(context)

    // El modo demo de la Fase 5 podrá reemplazar este reloj para simular otros días.
    private val clock: Clock = Clock.systemDefaultZone()

    val missionRepository = MissionRepository(database.missionDao())

    val progressRepository = ProgressRepository(database.missionDao(), database.completionDao(), clock)
}
