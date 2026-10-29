package com.appw.appwear.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appw.appwear.R
import com.appw.appwear.shared.engine.LevelProgress
import com.appw.appwear.shared.engine.XpEngine
import com.appw.appwear.ui.components.EmptyState
import com.appw.appwear.ui.components.labelRes
import com.appw.appwear.ui.theme.LevelUpTheme

/** T1 · Dashboard. */
@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    // TODO(Fase 1): leer el progreso desde ProgressRepository. Por ahora se muestra un jugador nuevo.
    val progress = XpEngine.progress(totalXp = 0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LevelCard(progress)
        EmptyState(
            title = stringResource(R.string.dashboard_empty_title),
            message = stringResource(R.string.dashboard_empty_message),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LevelCard(progress: LevelProgress, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.level_format, progress.level),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(XpEngine.titleFor(progress.level).labelRes()),
                style = MaterialTheme.typography.titleMedium,
            )
            LinearProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(R.string.xp_progress_format, progress.xpInLevel, progress.xpForNext),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    LevelUpTheme {
        DashboardScreen()
    }
}
