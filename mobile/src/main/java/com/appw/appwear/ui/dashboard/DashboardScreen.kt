package com.appw.appwear.ui.dashboard

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appw.appwear.R
import com.appw.appwear.data.repository.CompletionOutcome
import com.appw.appwear.shared.engine.LevelProgress
import com.appw.appwear.shared.engine.XpEngine
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.ui.components.EmptyState
import com.appw.appwear.ui.components.labelRes
import com.appw.appwear.ui.theme.LevelUpTheme

/** T1 · Dashboard. */
@Composable
fun DashboardScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resources = LocalResources.current

    LaunchedEffect(viewModel) {
        viewModel.completionOutcomes.collect { outcome ->
            snackbarHostState.showSnackbar(outcome.message(resources))
        }
    }

    DashboardContent(
        uiState = uiState,
        onCompleteMission = viewModel::completeMission,
        modifier = modifier,
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onCompleteMission: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { LevelCard(uiState.profile.progress) }

        if (!uiState.isLoading && uiState.todayMissions.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.dashboard_empty_title),
                    message = stringResource(R.string.dashboard_empty_message),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.today_missions_header),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            items(uiState.todayMissions, key = { it.mission.id }) { item ->
                TodayMissionCard(item = item, onComplete = { onCompleteMission(item.mission.id) })
            }
        }
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

@Composable
private fun TodayMissionCard(item: TodayMission, onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.mission.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(
                        R.string.mission_summary_format,
                        stringResource(item.mission.category.labelRes()),
                        stringResource(R.string.xp_reward_format, item.mission.difficulty.baseXp),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (item.isCompleted) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.mission_completed),
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            } else {
                FilledTonalButton(onClick = onComplete) {
                    Text(stringResource(R.string.action_complete))
                }
            }
        }
    }
}

private fun CompletionOutcome.message(resources: Resources): String = when (this) {
    is CompletionOutcome.Completed -> if (reward.leveledUp) {
        resources.getString(R.string.level_up_format, reward.levelAfter, reward.xpEarned)
    } else {
        resources.getString(R.string.completion_reward_format, reward.xpEarned, reward.coinsEarned)
    }
    CompletionOutcome.AlreadyCompleted -> resources.getString(R.string.mission_already_completed)
    CompletionOutcome.MissionNotFound -> resources.getString(R.string.mission_not_found)
}

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreview() {
    val study = Mission(
        id = "1",
        title = "Estudiar 25 min",
        category = Category.STUDY,
        difficulty = Difficulty.MEDIUM,
        recurrence = Recurrence.DAILY,
        createdAt = 0,
    )
    LevelUpTheme {
        DashboardContent(
            uiState = DashboardUiState(
                profile = PlayerProfile(totalXp = 180),
                todayMissions = listOf(
                    TodayMission(study, isCompleted = false),
                    TodayMission(
                        study.copy(id = "2", title = "Caminar 20 min", category = Category.EXERCISE),
                        isCompleted = true,
                    ),
                ),
                isLoading = false,
            ),
            onCompleteMission = {},
        )
    }
}
