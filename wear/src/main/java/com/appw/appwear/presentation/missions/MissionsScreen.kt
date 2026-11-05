package com.appw.appwear.presentation.missions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import com.appw.appwear.presentation.components.CategoryIcon
import com.appw.appwear.presentation.components.TintedIcon
import com.appw.appwear.presentation.components.XpLabel
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
            } else {
                item {
                    Text(
                        text = stringResource(
                            R.string.today_progress_format,
                            missions.count { it.isCompleted },
                            missions.size,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            items(missions, key = { it.mission.id }) { item ->
                MissionButton(
                    item = item,
                    onClick = { onMissionClick(item.mission.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                )
            }
        }
    }
}

@Composable
private fun MissionButton(
    item: TodayMission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        transformation = transformation,
        colors = ButtonDefaults.filledTonalButtonColors(),
        icon = {
            if (item.isCompleted) {
                TintedIcon(
                    iconRes = R.drawable.ic_check,
                    color = MaterialTheme.colorScheme.tertiary,
                    contentDescription = stringResource(R.string.mission_completed),
                )
            } else {
                CategoryIcon(category = item.mission.category)
            }
        },
        secondaryLabel = {
            if (item.isCompleted) {
                Text(
                    text = stringResource(R.string.mission_completed),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            } else {
                XpLabel(xp = item.mission.difficulty.baseXp)
            }
        },
    ) {
        Text(
            text = item.mission.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
            color = if (item.isCompleted) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
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
