package com.appw.appwear.sync

import android.util.Log
import com.appw.appwear.shared.engine.Badge
import com.appw.appwear.shared.sync.SyncPaths
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import kotlinx.coroutines.tasks.await

/**
 * Avisa al reloj de cada insignia nueva para que la celebre.
 *
 * Usa MessageClient, como indica el plan: si el reloj no está conectado el aviso se pierde,
 * pero no afecta los datos porque la galería del teléfono calcula las insignias del historial.
 */
class BadgeNotifier(
    private val messageClient: MessageClient,
    private val nodeClient: NodeClient,
) {
    suspend fun notify(badges: List<Badge>) {
        if (badges.isEmpty()) return
        try {
            val nodes = nodeClient.connectedNodes.await()
            for (badge in badges) {
                for (node in nodes) {
                    messageClient.sendMessage(node.id, SyncPaths.BADGE_NOTIFY, badge.name.encodeToByteArray()).await()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo avisar al reloj de $badges", e)
        }
    }

    private companion object {
        const val TAG = "BadgeNotifier"
    }
}
