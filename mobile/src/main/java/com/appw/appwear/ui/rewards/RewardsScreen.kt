package com.appw.appwear.ui.rewards

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState

/** T6 · Tienda de recompensas. */
@Composable
fun RewardsScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 3): crear recompensas, canjearlas con monedas e historial.
    EmptyState(
        title = stringResource(R.string.coming_soon),
        message = stringResource(R.string.rewards_placeholder),
        modifier = modifier.fillMaxSize(),
    )
}
