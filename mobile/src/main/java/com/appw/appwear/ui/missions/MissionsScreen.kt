package com.appw.appwear.ui.missions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState

/** T2 · Lista de misiones. */
@Composable
fun MissionsScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 1): lista de misiones con filtros por categoría.
    EmptyState(
        title = stringResource(R.string.missions_empty_title),
        message = stringResource(R.string.missions_empty_message),
        modifier = modifier.fillMaxSize(),
    )
}
