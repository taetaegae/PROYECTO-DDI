package com.appw.appwear.feedback

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Patrones de vibración del reloj (apartado 8 del plan). */
object Haptics {

    /** R5 · Vibración corta al completar una misión. */
    fun missionDone(context: Context) {
        // Pulso simple en vez de un efecto predefinido: no todos los relojes los soportan.
        vibrator(context).vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** R6 · Patrón largo, de pulsos cortos a uno largo, al subir de nivel. */
    fun levelUp(context: Context) {
        vibrator(context).vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 90, 70, 90, 70, 90, 120, 450), -1),
        )
    }

    /**
     * R4 · Tres pulsos al terminar el temporizador.
     *
     * Usa la categoría de alarma porque debe sentirse con la pantalla apagada y la app en segundo plano,
     * cuando Android puede ignorar las vibraciones normales.
     */
    fun timerDone(context: Context) {
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 350, 200, 350, 200, 350), -1)
        val vibrator = vibrator(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrator.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(effect, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
        }
    }

    private fun vibrator(context: Context): Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }
}
