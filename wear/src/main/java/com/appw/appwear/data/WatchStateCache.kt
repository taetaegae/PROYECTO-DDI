package com.appw.appwear.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.shared.sync.SyncJson
import com.appw.appwear.shared.sync.SyncPaths
import com.appw.appwear.shared.sync.toDto
import com.appw.appwear.shared.sync.toProfile
import com.appw.appwear.shared.sync.toTodayMission
import com.google.android.gms.wearable.DataClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private val Context.syncStore: DataStore<Preferences> by preferencesDataStore(name = "levelup_sync")

class WatchStateCache(private val context: Context) {
    val profile: Flow<PlayerProfile> = context.syncStore.data.map { prefs ->
        prefs[PROFILE_KEY]?.let { SyncJson.decodeProfile(it).toProfile() } ?: PlayerProfile()
    }
    val todayMissions: Flow<List<TodayMission>> = context.syncStore.data.map { prefs ->
        prefs[MISSIONS_KEY]?.let { json -> SyncJson.decodeMissions(json).map { it.toTodayMission() } } ?: emptyList()
    }
    suspend fun saveFromPhone(path: String?, json: String) {
        when (path) {
            SyncPaths.PROFILE -> context.syncStore.edit { it[PROFILE_KEY] = json }
            SyncPaths.MISSIONS -> context.syncStore.edit { it[MISSIONS_KEY] = json }
        }
    }
    suspend fun saveLocal(profile: PlayerProfile, missions: List<TodayMission>) {
        context.syncStore.edit { prefs ->
            prefs[PROFILE_KEY] = SyncJson.encodeProfile(profile.toDto())
            prefs[MISSIONS_KEY] = SyncJson.encodeMissions(missions.map { it.toDto() })
        }
    }
    suspend fun loadFromDataLayer(dataClient: DataClient) {
        val items = dataClient.dataItems.await()
        try { for (item in items) item.data?.let { saveFromPhone(item.uri.path, it.decodeToString()) } }
        finally { items.release() }
    }
    private companion object {
        val PROFILE_KEY = stringPreferencesKey("profile")
        val MISSIONS_KEY = stringPreferencesKey("missions")
    }
}
