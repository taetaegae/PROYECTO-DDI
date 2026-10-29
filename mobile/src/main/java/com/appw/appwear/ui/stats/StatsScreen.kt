package com.appw.appwear.ui.stats

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState

/** T4 · Estadísticas. */
@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 3): XP de los últimos 7 días y atributos.
    EmptyState(
        title = stringResource(R.string.coming_soon),
        message = stringResource(R.string.stats_placeholder),
        modifier = modifier.fillMaxSize(),
    )
}
