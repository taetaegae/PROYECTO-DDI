package com.appw.appwear.sync

import android.net.Uri
import android.util.Log
import com.appw.appwear.data.repository.ProgressRepository
import com.appw.appwear.data.settings.DemoSettings
import com.appw.appwear.shared.sync.SettingsDto
import com.appw.appwear.shared.sync.SyncJson
import com.appw.appwear.shared.sync.SyncPaths
import com.appw.appwear.shared.sync.toDto
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StatePublisher(
    private val dataClient: DataClient,
    private val progressRepository: ProgressRepository,
    private val demoSettings: DemoSettings,
) {
    fun start(scope: CoroutineScope) {
        scope.launch { progressRepository.profile.collect { publish(SyncPaths.PROFILE, SyncJson.encodeProfile(it.toDto())) } }
        scope.launch { progressRepository.todayMissions.collect { publish(SyncPaths.MISSIONS, SyncJson.encodeMissions(it.map { mission -> mission.toDto() })) } }
        scope.launch { demoSettings.demoMode.collect { publish(SyncPaths.SETTINGS, settingsJson(it)) } }
    }

    /**
     * Modo demo: vuelve a enviar todo el estado al reloj.
     *
     * El Data Layer no avisa al reloj si un dato llega idéntico, así que cada ruta
     * se borra y se publica de nuevo. El reloj ignora el borrado y guarda la nueva copia.
     */
    suspend fun forcePublish() {
        val state = listOf(
            SyncPaths.PROFILE to SyncJson.encodeProfile(progressRepository.profile.first().toDto()),
            SyncPaths.MISSIONS to SyncJson.encodeMissions(progressRepository.todayMissions.first().map { it.toDto() }),
            SyncPaths.SETTINGS to settingsJson(demoSettings.demoMode.value),
        )
        for ((path, json) in state) {
            val uri = Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).authority("*").path(path).build()
            try { dataClient.deleteDataItems(uri).await() } catch (e: Exception) { Log.w(TAG, "No se pudo borrar $path", e) }
            publish(path, json)
        }
    }

    private fun settingsJson(demoMode: Boolean): String = SyncJson.encodeSettings(SettingsDto(demoMode = demoMode))

    private suspend fun publish(path: String, json: String) {
        val request = PutDataRequest.create(path).setData(json.encodeToByteArray()).setUrgent()
        try { dataClient.putDataItem(request).await() } catch (e: Exception) { Log.w(TAG, "No se pudo publicar $path", e) }
    }

    private companion object { const val TAG = "StatePublisher" }
}
