package com.appw.appwear.ui.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appw.appwear.R
import com.appw.appwear.shared.engine.Badge
import com.appw.appwear.ui.components.color
import com.appw.appwear.ui.components.conditionRes
import com.appw.appwear.ui.components.iconRes
import com.appw.appwear.ui.components.nameRes
import com.appw.appwear.ui.theme.LevelUpTheme

/** T5 · Galería de insignias: las desbloqueadas a color y las bloqueadas en gris con su condición. */
@Composable
fun BadgesScreen(
    modifier: Modifier = Modifier,
    viewModel: BadgesViewModel = viewModel(factory = BadgesViewModel.Factory),
) {
    val unlocked by viewModel.unlocked.collectAsStateWithLifecycle()
    BadgesContent(unlocked = unlocked, modifier = modifier)
}

@Composable
fun BadgesContent(unlocked: Set<Badge>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.badges_progress_format, unlocked.size, Badge.entries.size),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        items(Badge.entries, key = { it.name }) { badge ->
            BadgeCard(badge = badge, isUnlocked = badge in unlocked)
        }
    }
}

@Composable
private fun BadgeCard(badge: Badge, isUnlocked: Boolean) {
    val name = stringResource(badge.nameRes())
    val condition = stringResource(badge.conditionRes())
    val state = stringResource(if (isUnlocked) R.string.badge_unlocked else R.string.badge_locked)
    val tint = if (isUnlocked) badge.color() else MaterialTheme.colorScheme.outline
    Card(
        // Una sola descripción por tarjeta para el lector de pantalla.
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "$name. $state. $condition" },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = if (isUnlocked) 0.18f else 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(badge.iconRes()),
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(30.dp),
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = condition,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgesContentPreview() {
    LevelUpTheme {
        BadgesContent(unlocked = setOf(Badge.FIRST_STEP, Badge.EARLY_BIRD, Badge.LEVEL_5))
    }
}
