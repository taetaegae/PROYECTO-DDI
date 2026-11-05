package com.appw.appwear.presentation.reward

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.model.CompletionReward

/** R5 · Misión cumplida. La vibración y la animación llegan en la Fase 3. */
@Composable
fun MissionDoneScreen(
    reward: CompletionReward?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenScaffold(modifier = modifier) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.mission_done_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
            )
            if (reward != null) {
                Text(
                    text = stringResource(R.string.mission_done_reward_format, reward.xpEarned, reward.coinsEarned),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                if (reward.leveledUp) {
                    Text(
                        text = stringResource(R.string.mission_done_level_up_format, reward.levelAfter),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Button(onClick = onDone, modifier = Modifier.padding(top = 8.dp)) {
                Text(stringResource(R.string.action_done))
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun MissionDoneScreenPreview() {
    LevelUpTheme {
        MissionDoneScreen(
            reward = CompletionReward(xpEarned = 28, coinsEarned = 5, levelBefore = 1, levelAfter = 2),
            onDone = {},
        )
    }
}
