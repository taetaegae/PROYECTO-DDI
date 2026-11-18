package com.appw.appwear.sync

import android.util.Log
import com.appw.appwear.LevelUpApp
import com.appw.appwear.data.repository.CompletionOutcome
import com.appw.appwear.shared.model.CompletionSource
import com.appw.appwear.shared.sync.SyncJson
import com.appw.appwear.shared.sync.SyncPaths
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class PhoneSyncService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        val events = dataEvents.filter { it.type == DataEvent.TYPE_CHANGED }
            .filter { it.dataItem.uri.path.orEmpty().startsWith(SyncPaths.COMPLETION_PREFIX) }
            .mapNotNull { event -> event.dataItem.data?.let { event.dataItem.uri to SyncJson.decodeEvent(it.decodeToString()) } }
        if (events.isEmpty()) return
        val progressRepository = (application as LevelUpApp).container.progressRepository
        val badgeNotifier = (application as LevelUpApp).container.badgeNotifier
        val dataClient = Wearable.getDataClient(this)
        runBlocking {
            for ((uri, event) in events) {
                val outcome = progressRepository.completeMission(event.missionId, CompletionSource.WATCH, event.sensorVerified, event.eventId, event.completedAt)
                Log.d(TAG, "Evento ${event.eventId} de la misión ${event.missionId}: $outcome")
                if (outcome is CompletionOutcome.Completed) badgeNotifier.notify(outcome.newBadges)
                dataClient.deleteDataItems(uri).await()
            }
        }
    }

    private companion object { const val TAG = "PhoneSyncService" }
}
