package com.appw.appwear

import android.app.Application
import com.appw.appwear.data.SyncedWatchRepository
import com.appw.appwear.data.WatchRepository
import com.appw.appwear.data.WatchStateCache
import com.appw.appwear.sync.EventSender
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LevelUpWearApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val cache: WatchStateCache by lazy { WatchStateCache(this) }

    // TODO(Fase 2): reemplazar por el repositorio que lee la caché sincronizada con el teléfono.
    val repository: WatchRepository by lazy {
        SyncedWatchRepository(cache, EventSender(Wearable.getDataClient(this)), appScope)
    }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { cache.loadFromDataLayer(Wearable.getDataClient(this@LevelUpWearApp)) }
    }
}
