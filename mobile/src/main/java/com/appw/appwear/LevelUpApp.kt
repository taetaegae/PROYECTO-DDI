package com.appw.appwear

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras

class LevelUpApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Acceso al contenedor desde las fábricas de los ViewModel. */
fun CreationExtras.appContainer(): AppContainer = (this[APPLICATION_KEY] as LevelUpApp).container
