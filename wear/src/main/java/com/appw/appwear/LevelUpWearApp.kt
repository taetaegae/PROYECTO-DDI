package com.appw.appwear

import android.app.Application
import com.appw.appwear.data.FakeMissionRepository
import com.appw.appwear.data.WatchRepository

class LevelUpWearApp : Application() {

    // TODO(Fase 2): reemplazar por el repositorio que lee la caché sincronizada con el teléfono.
    val repository: WatchRepository by lazy { FakeMissionRepository() }
}
