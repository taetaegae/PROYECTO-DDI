package com.appw.appwear.presentation.reward

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.components.TintedIcon
import com.appw.appwear.presentation.theme.CoinGold
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.model.CompletionReward

/** R5 · Misión cumplida. La vibración y la animación llegan en la Fase 3. */
@Composable
fun MissionDoneScreen(
    reward: CompletionReward?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberTransformingLazyColumnState()
    val leveledUp = reward?.leveledUp == true

    ScreenScaffold(
        scrollState = listState,
        modifier = modifier,
        edgeButton = {
            EdgeButton(onClick = onDone, buttonSize = EdgeButtonSize.Small) {
                Text(stringResource(R.string.action_done))
            }
        },
    ) { contentPadding ->
        TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
            item {
                // Trofeo dorado si subió de nivel; palomita verde en cualquier otra misión.
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TintedIcon(
                        iconRes = if (leveledUp) R.drawable.ic_trophy else R.drawable.ic_check,
                        color = if (leveledUp) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                        contentDescription = null,
                        size = 36.dp,
                    )
                }
            }
            item {
                Text(
                    text = stringResource(R.string.mission_done_title),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            }
            if (reward != null) {
                item {
                    Text(
                        text = stringResource(R.string.xp_reward_format, reward.xpEarned),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_coin),
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = stringResource(R.string.coins_reward_format, reward.coinsEarned),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                if (reward.leveledUp) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.mission_done_level_up_format, reward.levelAfter),
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun MissionDoneScreenPreview() {
    LevelUpTheme {
        MissionDoneScreen(
            reward = CompletionReward(xpEarned = 30, coinsEarned = 6, levelBefore = 1, levelAfter = 2),
            onDone = {},
        )
    }
}
