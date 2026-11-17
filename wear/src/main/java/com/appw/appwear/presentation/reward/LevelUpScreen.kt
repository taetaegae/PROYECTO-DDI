package com.appw.appwear.presentation.reward

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.CircularProgressIndicatorDefaults
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.components.TintedIcon
import com.appw.appwear.presentation.components.labelRes
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.engine.XpEngine
import kotlinx.coroutines.launch

/** R6 · Subida de nivel: el anillo se llena, el trofeo rebota y el fondo brilla. */
@Composable
fun LevelUpScreen(level: Int, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val gold = MaterialTheme.colorScheme.secondary
    val ring = remember { Animatable(0f) }
    val trophyScale = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        launch { ring.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing)) }
        trophyScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        )
    }
    val glow by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.2f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1100), RepeatMode.Reverse),
        label = "glowAlpha",
    )

    ScreenScaffold(modifier = modifier) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(gold.copy(alpha = glow), Color.Transparent))),
        )
        CircularProgressIndicator(
            progress = { ring.value },
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
            TintedIcon(
                iconRes = R.drawable.ic_trophy,
                color = gold,
                contentDescription = null,
                size = 40.dp,
                modifier = Modifier.graphicsLayer {
                    scaleX = trophyScale.value
                    scaleY = trophyScale.value
                },
            )
            val description = stringResource(R.string.level_up_description, level)
            Column(
                modifier = Modifier.clearAndSetSemantics { contentDescription = description },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.level_up_label),
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = gold,
                )
                Text(text = level.toString(), style = MaterialTheme.typography.displayMedium)
            }
            Text(
                text = stringResource(XpEngine.titleFor(level).labelRes()),
                style = MaterialTheme.typography.titleSmall,
                color = gold,
            )
            CompactButton(
                onClick = onDone,
                modifier = Modifier.padding(top = 6.dp),
                label = { Text(stringResource(R.string.action_continue)) },
            )
        }
    }
}

@WearPreviewDevices
@Composable
private fun LevelUpScreenPreview() {
    LevelUpTheme {
        LevelUpScreen(level = 5, onDone = {})
    }
}
