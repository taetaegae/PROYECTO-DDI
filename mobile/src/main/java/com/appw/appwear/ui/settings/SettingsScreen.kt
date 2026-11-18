package com.appw.appwear.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appw.appwear.R
import com.appw.appwear.ui.components.EmptyState
import com.appw.appwear.ui.theme.LevelUpTheme

/** T7 · Ajustes. En la versión de depuración incluye el modo demo para la exposición. */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (!uiState.isDebugBuild) {
        EmptyState(
            title = stringResource(R.string.coming_soon),
            message = stringResource(R.string.settings_placeholder),
            modifier = modifier.fillMaxSize(),
        )
        return
    }
    DemoModeContent(
        uiState = uiState,
        onDemoModeChange = viewModel::setDemoMode,
        onPrepareLevelUp = viewModel::prepareLevelUp,
        onLoadSamples = viewModel::loadSampleMissions,
        onForceSync = viewModel::forceSync,
        modifier = modifier,
    )
}

@Composable
fun DemoModeContent(
    uiState: SettingsUiState,
    onDemoModeChange: (Boolean) -> Unit,
    onPrepareLevelUp: () -> Unit,
    onLoadSamples: () -> Unit,
    onForceSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = stringResource(R.string.demo_title), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.demo_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Toda la fila cambia el interruptor, y el lector de pantalla la anuncia como uno solo.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = uiState.demoMode, role = Role.Switch, onValueChange = onDemoModeChange)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.demo_timer_title), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(R.string.demo_timer_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = uiState.demoMode, onCheckedChange = null)
        }
        OutlinedButton(onClick = onPrepareLevelUp, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.demo_prepare_level_up))
        }
        OutlinedButton(onClick = onLoadSamples, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.demo_load_samples))
        }
        OutlinedButton(onClick = onForceSync, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.demo_force_sync))
        }
        val message = when (val current = uiState.message) {
            is DemoMessage.LevelUpReady -> stringResource(R.string.demo_level_up_ready, current.nextLevel)
            DemoMessage.SamplesLoaded -> stringResource(R.string.demo_samples_loaded)
            DemoMessage.SyncSent -> stringResource(R.string.demo_sync_sent)
            null -> null
        }
        if (message != null) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DemoModeContentPreview() {
    LevelUpTheme {
        DemoModeContent(
            uiState = SettingsUiState(isDebugBuild = true, demoMode = true, message = DemoMessage.LevelUpReady(4)),
            onDemoModeChange = {},
            onPrepareLevelUp = {},
            onLoadSamples = {},
            onForceSync = {},
        )
    }
}
