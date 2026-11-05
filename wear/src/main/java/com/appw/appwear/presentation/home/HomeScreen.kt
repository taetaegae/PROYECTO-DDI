package com.appw.appwear.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.CircularProgressIndicatorDefaults
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.components.labelRes
import com.appw.appwear.presentation.theme.CoinGold
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.presentation.theme.StreakFlame
import com.appw.appwear.presentation.theme.Violet
import com.appw.appwear.shared.model.PlayerProfile

/** R1 · Inicio: anillo de XP en el borde de la pantalla con el nivel al centro. */
@Composable
fun HomeScreen(profile: PlayerProfile, onOpenMissions: () -> Unit, modifier: Modifier = Modifier) {
    val progress = profile.progress
    val fraction by animateFloatAsState(targetValue = progress.fraction, label = "xp")
    val gold = MaterialTheme.colorScheme.secondary

    ScreenScaffold(modifier = modifier) { contentPadding ->
        // Brillo violeta detrás del nivel.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Violet.copy(alpha = 0.45f), Color.Transparent))),
        )
        CircularProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxSize()
                .padding(CircularProgressIndicatorDefaults.FullScreenPadding),
            colors = ProgressIndicatorDefaults.colors(
                indicatorColor = gold,
                trackColor = gold.copy(alpha = 0.2f),
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val levelDescription = stringResource(R.string.level_format, progress.level)
            Column(
                modifier = Modifier.clearAndSetSemantics { contentDescription = levelDescription },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.level_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = progress.level.toString(),
                    style = MaterialTheme.typography.displayMedium,
                )
            }
            Text(
                text = stringResource(profile.title.labelRes()),
                style = MaterialTheme.typography.titleSmall,
                color = gold,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatLabel(
                    iconRes = R.drawable.ic_streak,
                    tint = StreakFlame,
                    value = profile.streakDays,
                    description = pluralStringResource(
                        R.plurals.streak_days_format,
                        profile.streakDays,
                        profile.streakDays,
                    ),
                )
                StatLabel(
                    iconRes = R.drawable.ic_coin,
                    tint = CoinGold,
                    value = profile.coins,
                    description = stringResource(R.string.coins_format, profile.coins),
                )
            }
            CompactButton(
                onClick = onOpenMissions,
                modifier = Modifier.padding(top = 6.dp),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_bolt),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.ExtraSmallIconSize),
                    )
                },
                label = { Text(stringResource(R.string.action_missions)) },
            )
        }
    }
}

@Composable
private fun StatLabel(
    @DrawableRes iconRes: Int,
    tint: Color,
    value: Int,
    description: String,
) {
    Row(
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp),
        )
        Text(text = value.toString(), style = MaterialTheme.typography.labelMedium)
    }
}

@WearPreviewDevices
@Composable
private fun HomeScreenPreview() {
    LevelUpTheme {
        HomeScreen(profile = PlayerProfile(totalXp = 180, streakDays = 3, coins = 42), onOpenMissions = {})
    }
}
