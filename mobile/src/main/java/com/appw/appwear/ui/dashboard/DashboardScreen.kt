package com.appw.appwear.ui.dashboard

import android.content.res.Resources
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appw.appwear.R
import com.appw.appwear.data.repository.CompletionOutcome
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.TodayMission
import com.appw.appwear.ui.components.CategoryIcon
import com.appw.appwear.ui.components.XpBadge
import com.appw.appwear.ui.components.labelRes
import com.appw.appwear.ui.theme.CoinGold
import com.appw.appwear.ui.theme.HeroEnd
import com.appw.appwear.ui.theme.HeroStart
import com.appw.appwear.ui.theme.LevelUpTheme
import com.appw.appwear.ui.theme.StreakFlame
import com.appw.appwear.ui.theme.XpGold

/** T1 · Dashboard. */
@Composable
fun DashboardScreen(
    snackbarHostState: SnackbarHostState,
    onCreateMission: () -> Unit,
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
        onCreateMission = onCreateMission,
        modifier = modifier,
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onCompleteMission: (String) -> Unit,
    onCreateMission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Mientras carga no se dibuja nada, para no mostrar un instante el nivel 1 con 0 XP.
    if (uiState.isLoading) return

    val missions = uiState.todayMissions
    val completedCount = missions.count { it.isCompleted }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { LevelHeroCard(uiState.profile) }
        item {
            StatsRow(
                streakDays = uiState.profile.streakDays,
                coins = uiState.profile.coins,
                completedToday = completedCount,
                totalToday = missions.size,
            )
        }

        if (missions.isEmpty()) {
            item { NoMissionsCard(onCreateMission = onCreateMission) }
        } else {
            item { SectionHeader(completed = completedCount, total = missions.size) }
            if (missions.isNotEmpty() && completedCount == missions.size) {
                item { AllDoneBanner() }
            }
            items(missions, key = { it.mission.id }) { item ->
                TodayMissionCard(
                    item = item,
                    onComplete = { onCompleteMission(item.mission.id) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

/** Tarjeta principal con el nivel, el título y el avance de XP. */
@Composable
private fun LevelHeroCard(profile: PlayerProfile, modifier: Modifier = Modifier) {
    val progress = profile.progress
    val fraction by animateFloatAsState(targetValue = progress.fraction, label = "xp")
    val subtleWhite = Color.White.copy(alpha = 0.8f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(HeroStart, HeroEnd)))
            .padding(20.dp),
    ) {
        // Rayo decorativo de fondo. Va en una capa del tamaño de la tarjeta para no cambiar su alto,
        // y la tarjeta recorta lo que sobresale.
        Box(modifier = Modifier.matchParentSize()) {
            Icon(
                painter = painterResource(R.drawable.ic_bolt),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-20).dp)
                    .requiredSize(150.dp),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            LevelRing(level = progress.level, fraction = fraction)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(profile.title.labelRes()),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                )
                Text(
                    text = stringResource(
                        R.string.xp_to_next_level_format,
                        progress.xpForNext - progress.xpInLevel,
                        progress.level + 1,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtleWhite,
                )
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(10.dp),
                    color = XpGold,
                    trackColor = Color.White.copy(alpha = 0.22f),
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Text(
                    text = stringResource(R.string.xp_progress_format, progress.xpInLevel, progress.xpForNext),
                    style = MaterialTheme.typography.labelMedium,
                    color = subtleWhite,
                )
            }
        }
    }
}

/** Número de nivel rodeado por un anillo dorado que se llena con la XP. */
@Composable
private fun LevelRing(level: Int, fraction: Float, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.level_format, level)
    Box(
        modifier = modifier
            .size(92.dp)
            .clearAndSetSemantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxSize(),
            color = XpGold,
            strokeWidth = 8.dp,
            trackColor = Color.White.copy(alpha = 0.22f),
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.level_label),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
            )
            Text(
                text = level.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 34.sp,
                lineHeight = 36.sp,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun StatsRow(
    streakDays: Int,
    coins: Int,
    completedToday: Int,
    totalToday: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StatCard(
            icon = painterResource(R.drawable.ic_streak),
            tint = StreakFlame,
            value = streakDays.toString(),
            label = pluralStringResource(R.plurals.streak_days_label, streakDays),
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        StatCard(
            icon = painterResource(R.drawable.ic_coin),
            tint = CoinGold,
            value = coins.toString(),
            label = stringResource(R.string.stat_coins),
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        StatCard(
            icon = rememberVectorPainter(Icons.Filled.CheckCircle),
            tint = MaterialTheme.colorScheme.tertiary,
            value = stringResource(R.string.today_progress_short_format, completedToday, totalToday),
            label = stringResource(R.string.stat_done_today),
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
private fun StatCard(
    icon: Painter,
    tint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionHeader(completed: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.today_missions_header),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        if (total > 0) {
            Text(
                text = stringResource(R.string.today_progress_format, completed, total),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TodayMissionCard(item: TodayMission, onComplete: () -> Unit, modifier: Modifier = Modifier) {
    val mission = item.mission
    Card(
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                    color = if (item.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    XpBadge(xp = mission.difficulty.baseXp)
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
            }
            CompleteButton(isCompleted = item.isCompleted, onClick = onComplete)
        }
    }
}

/** Círculo para completar la misión; se rellena de verde cuando ya está cumplida. */
@Composable
private fun CompleteButton(isCompleted: Boolean, onClick: () -> Unit) {
    if (isCompleted) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = stringResource(R.string.mission_completed),
                tint = MaterialTheme.colorScheme.onTertiary,
            )
        }
    } else {
        OutlinedIconButton(
            onClick = onClick,
            modifier = Modifier.size(44.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = stringResource(R.string.action_complete),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun AllDoneBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(XpGold.copy(alpha = 0.22f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_trophy),
            contentDescription = null,
            tint = CoinGold,
            modifier = Modifier.size(36.dp),
        )
        Column {
            Text(text = stringResource(R.string.all_done_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.all_done_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NoMissionsCard(onCreateMission: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_bolt),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = stringResource(R.string.dashboard_empty_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.dashboard_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onCreateMission, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.action_create_mission))
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
                profile = PlayerProfile(totalXp = 180, streakDays = 3, coins = 42),
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
            onCreateMission = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardEmptyPreview() {
    LevelUpTheme {
        DashboardContent(
            uiState = DashboardUiState(isLoading = false),
            onCompleteMission = {},
            onCreateMission = {},
        )
    }
}
