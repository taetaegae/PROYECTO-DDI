package com.appw.appwear.presentation.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.CircularProgressIndicatorDefaults
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.FilledTonalIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.timer.TimerState
import kotlinx.coroutines.delay

/** R4 · Temporizador: cuenta regresiva circular con pausar y cancelar. */
@Composable
fun TimerScreen(
    state: TimerState?,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(state) {
        // Solo se redibuja mientras corre; el tiempo real lo calcula TimerState con la hora de término.
        while (state != null && !state.isPaused) {
            now = System.currentTimeMillis()
            delay(TICK_MILLIS)
        }
    }

    ScreenScaffold(modifier = modifier) { contentPadding ->
        if (state == null) {
            // El temporizador terminó o se canceló; la navegación ya va hacia otra pantalla.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.timer_finished), textAlign = TextAlign.Center)
            }
            return@ScreenScaffold
        }

        val remaining = state.remainingMillis(now)
        val ringColor = if (state.isPaused) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.secondary
        }

        CircularProgressIndicator(
            // Lee el tiempo dentro de la lambda para que el anillo se redibuje en cada tic.
            progress = {
                if (state.durationMillis > 0) state.remainingMillis(now).toFloat() / state.durationMillis else 0f
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(CircularProgressIndicatorDefaults.FullScreenPadding),
            colors = ProgressIndicatorDefaults.colors(
                indicatorColor = ringColor,
                trackColor = ringColor.copy(alpha = 0.2f),
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = state.missionTitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val timeText = formatRemaining(remaining)
            val timeDescription = stringResource(R.string.timer_remaining_description, timeText)
            Text(
                text = timeText,
                modifier = Modifier.semantics { contentDescription = timeDescription },
                style = MaterialTheme.typography.displayMedium,
            )
            Text(
                text = stringResource(if (state.isPaused) R.string.timer_paused else R.string.timer_running),
                style = MaterialTheme.typography.labelSmall,
                color = ringColor,
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FilledTonalIconButton(onClick = onCancel) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.action_cancel),
                        modifier = Modifier.size(24.dp),
                    )
                }
                FilledIconButton(onClick = if (state.isPaused) onResume else onPause) {
                    Icon(
                        painter = painterResource(if (state.isPaused) R.drawable.ic_play else R.drawable.ic_pause),
                        contentDescription = stringResource(
                            if (state.isPaused) R.string.action_resume else R.string.action_pause,
                        ),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

/** "24:59". Redondea hacia arriba para que el último segundo muestre 0:01 y no 0:00. */
fun formatRemaining(remainingMillis: Long): String {
    val totalSeconds = (remainingMillis + 999) / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

private const val TICK_MILLIS = 250L

@WearPreviewDevices
@Composable
private fun TimerScreenPreview() {
    LevelUpTheme {
        TimerScreen(
            state = TimerState(
                missionId = "preview",
                missionTitle = "Estudiar 25 min",
                durationMillis = 25 * 60_000L,
                endsAtMillis = null,
                pausedRemainingMillis = 17 * 60_000L + 42_000L,
            ),
            onPause = {},
            onResume = {},
            onCancel = {},
        )
    }
}
