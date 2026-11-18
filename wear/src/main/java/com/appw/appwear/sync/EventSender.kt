package com.appw.appwear.sync

import android.util.Log
import com.appw.appwear.shared.sync.CompletionEventDto
import com.appw.appwear.shared.sync.SyncJson
import com.appw.appwear.shared.sync.SyncPaths
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EventSender(private val dataClient: DataClient) {
    suspend fun sendCompletion(missionId: String, sensorVerified: Boolean = false) {
        val event = CompletionEventDto(UUID.randomUUID().toString(), missionId, System.currentTimeMillis(), sensorVerified)
        val request = PutDataRequest.create(SyncPaths.completion(event.eventId))
            .setData(SyncJson.encodeEvent(event).encodeToByteArray()).setUrgent()
        try { dataClient.putDataItem(request).await() } catch (e: Exception) { Log.w(TAG, "No se pudo enviar el evento ${event.eventId}", e) }
    }
    private companion object { const val TAG = "EventSender" }
}
