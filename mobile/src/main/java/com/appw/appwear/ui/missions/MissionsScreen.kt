package com.appw.appwear.ui.missions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appw.appwear.R
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.ui.components.CategoryChipIcon
import com.appw.appwear.ui.components.CategoryIcon
import com.appw.appwear.ui.components.EmptyState
import com.appw.appwear.ui.components.XpBadge
import com.appw.appwear.ui.components.labelRes
import com.appw.appwear.ui.theme.LevelUpTheme

/** T2 · Lista de misiones. */
@Composable
fun MissionsScreen(
    onMissionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MissionsViewModel = viewModel(factory = MissionsViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MissionsContent(
        uiState = uiState,
        onCategorySelected = viewModel::selectCategory,
        onMissionClick = onMissionClick,
        modifier = modifier,
    )
}

@Composable
fun MissionsContent(
    uiState: MissionsUiState,
    onCategorySelected: (Category?) -> Unit,
    onMissionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        CategoryFilterRow(
            selected = uiState.selectedCategory,
            onSelected = onCategorySelected,
            modifier = Modifier.padding(top = 8.dp),
        )
        if (!uiState.isLoading && uiState.missions.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.missions_empty_title),
                message = stringResource(
                    if (uiState.selectedCategory == null) R.string.missions_empty_message
                    else R.string.missions_empty_filtered,
                ),
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.missions, key = { it.id }) { mission ->
                    MissionCard(mission = mission, onClick = { onMissionClick(mission.id) })
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: Category?,
    onSelected: (Category?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelected(null) },
                label = { Text(stringResource(R.string.filter_all)) },
            )
        }
        items(Category.entries) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelected(category) },
                label = { Text(stringResource(category.labelRes())) },
                leadingIcon = { CategoryChipIcon(category) },
            )
        }
    }
}

@Composable
private fun MissionCard(mission: Mission, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CategoryIcon(category = mission.category)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(
                        R.string.mission_summary_format,
                        stringResource(mission.difficulty.labelRes()),
                        stringResource(mission.recurrence.labelRes()),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            XpBadge(xp = mission.difficulty.baseXp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MissionsContentPreview() {
    LevelUpTheme {
        MissionsContent(
            uiState = MissionsUiState(
                missions = listOf(
                    Mission(
                        id = "1",
                        title = "Estudiar 25 min",
                        category = Category.STUDY,
                        difficulty = Difficulty.MEDIUM,
                        recurrence = Recurrence.DAILY,
                        createdAt = 0,
                    ),
                ),
                isLoading = false,
            ),
            onCategorySelected = {},
            onMissionClick = {},
        )
    }
}
