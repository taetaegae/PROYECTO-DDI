package com.appw.appwear.presentation.missions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
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
import com.appw.appwear.presentation.components.XpBadge
import com.appw.appwear.presentation.components.labelRes
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.model.TodayMission

/** R3 · Detalle de misión con el botón para completarla. */
@Composable
fun MissionDetailScreen(
    item: TodayMission?,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(
        scrollState = listState,
        modifier = modifier,
        edgeButton = {
            EdgeButton(
                onClick = onComplete,
                buttonSize = EdgeButtonSize.Medium,
                enabled = item != null && !item.isCompleted,
            ) {
                Text(
                    stringResource(
                        if (item?.isCompleted == true) R.string.mission_completed else R.string.action_complete,
                    ),
                )
            }
        },
    ) { contentPadding ->
        TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
            if (item == null) {
                item {
                    Text(
                        text = stringResource(R.string.mission_not_found),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                val mission = item.mission
                item {
                    ListHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(mission.title, textAlign = TextAlign.Center)
                    }
                }
                item {
                    // Categoría y XP en una sola fila para que el botón Completar quepa sin desplazar.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CategoryIcon(category = mission.category, size = 30.dp)
                        XpBadge(xp = mission.difficulty.baseXp)
                    }
                }
                item {
                    Text(
                        text = stringResource(
                            R.string.mission_summary_format,
                            stringResource(mission.difficulty.labelRes()),
                            stringResource(mission.recurrence.labelRes()),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                if (mission.description.isNotBlank()) {
                    item {
                        Text(
                            text = mission.description,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun MissionDetailScreenPreview() {
    LevelUpTheme {
        MissionDetailScreen(
            item = TodayMission(FakeMissionRepository.sampleMissions.first(), isCompleted = false),
            onComplete = {},
        )
    }
}
