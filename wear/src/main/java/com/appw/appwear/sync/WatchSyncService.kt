package com.appw.appwear.sync

import com.appw.appwear.LevelUpWearApp
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

class WatchSyncService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        val updates = dataEvents.filter { it.type == DataEvent.TYPE_CHANGED }.mapNotNull { event ->
            event.dataItem.data?.let { event.dataItem.uri.path to it.decodeToString() }
        }
        if (updates.isEmpty()) return
        val cache = (application as LevelUpWearApp).cache
        runBlocking { for ((path, json) in updates) cache.saveFromPhone(path, json) }
    }
}
