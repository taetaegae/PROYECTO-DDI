package com.appw.appwear.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.CircularProgressIndicatorDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.components.labelRes
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.model.PlayerProfile

/** R1 · Inicio: anillo de XP en el borde de la pantalla con el nivel al centro. */
@Composable
fun HomeScreen(profile: PlayerProfile, onOpenMissions: () -> Unit, modifier: Modifier = Modifier) {
    val progress = profile.progress

    ScreenScaffold(modifier = modifier) { contentPadding ->
        CircularProgressIndicator(
            progress = { progress.fraction },
            modifier = Modifier
                .fillMaxSize()
                .padding(CircularProgressIndicatorDefaults.FullScreenPadding),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.level_format, progress.level),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(profile.title.labelRes()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(R.string.xp_progress_format, progress.xpInLevel, progress.xpForNext),
                style = MaterialTheme.typography.bodySmall,
            )
            Button(
                onClick = onOpenMissions,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(stringResource(R.string.action_missions))
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun HomeScreenPreview() {
    LevelUpTheme {
        HomeScreen(profile = PlayerProfile(totalXp = 180), onOpenMissions = {})
    }
}
