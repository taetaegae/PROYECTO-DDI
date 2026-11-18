package com.appw.appwear.sync

import android.util.Log
import com.appw.appwear.data.repository.ProgressRepository
import com.appw.appwear.shared.sync.SyncJson
import com.appw.appwear.shared.sync.SyncPaths
import com.appw.appwear.shared.sync.toDto
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StatePublisher(private val dataClient: DataClient, private val progressRepository: ProgressRepository) {
    fun start(scope: CoroutineScope) {
        scope.launch { progressRepository.profile.collect { publish(SyncPaths.PROFILE, SyncJson.encodeProfile(it.toDto())) } }
        scope.launch { progressRepository.todayMissions.collect { publish(SyncPaths.MISSIONS, SyncJson.encodeMissions(it.map { mission -> mission.toDto() })) } }
    }

    private suspend fun publish(path: String, json: String) {
        val request = PutDataRequest.create(path).setData(json.encodeToByteArray()).setUrgent()
        try { dataClient.putDataItem(request).await() } catch (e: Exception) { Log.w(TAG, "No se pudo publicar $path", e) }
    }

    private companion object { const val TAG = "StatePublisher" }
}
