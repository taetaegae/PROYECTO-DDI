package com.appw.appwear.sync

import com.appw.appwear.LevelUpWearApp
import com.appw.appwear.notify.BadgeInbox
import com.appw.appwear.shared.engine.Badge
import com.appw.appwear.shared.sync.SyncPaths
import com.google.android.gms.wearable.MessageEvent
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

    /** Aviso del teléfono: insignia recién desbloqueada. La UI la celebra cuando puede. */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != SyncPaths.BADGE_NOTIFY) return
        val id = messageEvent.data.decodeToString()
        Badge.entries.find { it.name == id }?.let(BadgeInbox::add)
    }
}
