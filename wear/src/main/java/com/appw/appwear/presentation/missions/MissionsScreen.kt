package com.appw.appwear.presentation.missions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.data.FakeMissionRepository
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.model.TodayMission

/** R2 · Misiones de hoy. */
@Composable
fun MissionsScreen(
    missions: List<TodayMission>,
    onMissionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState, modifier = modifier) { contentPadding ->
        TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
            item {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text(stringResource(R.string.missions_header))
                }
            }
            if (missions.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.missions_empty),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            items(missions, key = { it.mission.id }) { item ->
                Button(
                    onClick = { onMissionClick(item.mission.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                    colors = if (item.isCompleted) {
                        ButtonDefaults.filledTonalButtonColors()
                    } else {
                        ButtonDefaults.buttonColors()
                    },
                    secondaryLabel = {
                        Text(
                            if (item.isCompleted) {
                                stringResource(R.string.mission_completed)
                            } else {
                                stringResource(R.string.xp_reward_format, item.mission.difficulty.baseXp)
                            },
                        )
                    },
                ) {
                    Text(item.mission.title, maxLines = 2)
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun MissionsScreenPreview() {
    LevelUpTheme {
        MissionsScreen(
            missions = FakeMissionRepository.sampleMissions.mapIndexed { index, mission ->
                TodayMission(mission, isCompleted = index == 0)
            },
            onMissionClick = {},
        )
    }
}
