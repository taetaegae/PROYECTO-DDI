package com.appw.appwear.ui.missions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState

/** T3 · Crear o editar misión. */
@Composable
fun MissionEditorScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 1): formulario con título, categoría, dificultad, recurrencia, verificación y meta.
    EmptyState(
        title = stringResource(R.string.coming_soon),
        message = stringResource(R.string.mission_editor_placeholder),
        modifier = modifier.fillMaxSize(),
    )
}
