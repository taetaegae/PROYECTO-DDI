package com.appw.appwear.ui.badges

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState

/** T5 · Galería de insignias. */
@Composable
fun BadgesScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 3): galería con insignias bloqueadas y desbloqueadas.
    EmptyState(
        title = stringResource(R.string.coming_soon),
        message = stringResource(R.string.badges_placeholder),
        modifier = modifier.fillMaxSize(),
    )
}
