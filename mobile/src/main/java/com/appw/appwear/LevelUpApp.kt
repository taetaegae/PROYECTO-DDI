package com.appw.appwear

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class LevelUpApp : Application() {

    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.statePublisher.start(appScope)
    }
}

/** Acceso al contenedor desde las fábricas de los ViewModel. */
fun CreationExtras.appContainer(): AppContainer = (this[APPLICATION_KEY] as LevelUpApp).container
