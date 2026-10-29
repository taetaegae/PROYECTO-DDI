package com.appw.appwear.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState

/** T7 · Ajustes. */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 5): nombre del jugador, recordatorios y modo demo.
    EmptyState(
        title = stringResource(R.string.coming_soon),
        message = stringResource(R.string.settings_placeholder),
        modifier = modifier.fillMaxSize(),
    )
}
