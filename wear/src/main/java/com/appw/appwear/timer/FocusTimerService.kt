package com.appw.appwear.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import com.appw.appwear.R
import com.appw.appwear.presentation.MainActivity

/**
 * Servicio en primer plano mientras corre el temporizador.
 *
 * Mantiene viva la app con la pantalla apagada y sostiene un wake lock parcial hasta la hora de término,
 * para que la misión se complete a tiempo. La cuenta en sí la lleva [FocusTimer].
 */
class FocusTimerService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timer = FocusTimer.get(this).state.value
        // Siempre se llama a startForeground, aunque el temporizador ya no exista, para cumplir
        // con startForegroundService(); después el servicio se detiene solo.
        startInForeground(buildNotification(timer))
        if (timer == null) {
            stopSelf()
        } else {
            holdWakeLock(timer)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        releaseWakeLock()
        super.onDestroy()
    }

    private fun startInForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun holdWakeLock(timer: TimerState) {
        releaseWakeLock()
        if (timer.isPaused) return
        val remaining = timer.remainingMillis(System.currentTimeMillis())
        wakeLock = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
            .apply {
                setReferenceCounted(false)
                // Con margen y con límite: si algo falla, no se queda tomado para siempre.
                acquire(remaining + WAKE_LOCK_MARGIN_MILLIS)
            }
    }

    private fun releaseWakeLock() {
        wakeLock?.takeIf { it.isHeld }?.release()
        wakeLock = null
    }

    private fun buildNotification(timer: TimerState?): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.timer_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        val openApp = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val builder = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle(timer?.missionTitle ?: getString(R.string.app_name))
            .setContentIntent(openApp)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
        val endsAt = timer?.endsAtMillis
        if (endsAt != null) {
            // Cuenta regresiva que el sistema dibuja solo, sin despertar a la app.
            builder.setContentText(getString(R.string.timer_running))
                .setShowWhen(true)
                .setWhen(endsAt)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
        } else if (timer != null) {
            builder.setContentText(getString(R.string.timer_paused))
        }
        return builder.build()
    }

    companion object {
        private const val CHANNEL_ID = "focus_timer"
        private const val NOTIFICATION_ID = 25
        private const val WAKE_LOCK_TAG = "LevelUp:FocusTimer"
        private const val WAKE_LOCK_MARGIN_MILLIS = 60_000L

        fun start(context: Context) {
            try {
                context.startForegroundService(Intent(context, FocusTimerService::class.java))
            } catch (e: IllegalStateException) {
                // Android no deja iniciarlo con la app en segundo plano. La cuenta sigue en
                // FocusTimer; solo falta la notificación hasta la siguiente acción del usuario.
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FocusTimerService::class.java))
        }
    }
}
