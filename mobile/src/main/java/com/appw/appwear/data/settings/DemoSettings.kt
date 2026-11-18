package com.appw.appwear.data.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Ajustes del modo demo (T7). Se guardan en el teléfono y se publican al reloj. */
class DemoSettings(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _demoMode = MutableStateFlow(prefs.getBoolean(KEY_DEMO_MODE, false))

    /** Con el modo demo, el temporizador del reloj dura 10 segundos. */
    val demoMode: StateFlow<Boolean> = _demoMode.asStateFlow()

    fun setDemoMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEMO_MODE, enabled).apply()
        _demoMode.value = enabled
    }

    private companion object {
        const val PREFS_NAME = "demo_settings"
        const val KEY_DEMO_MODE = "demo_mode"
    }
}
