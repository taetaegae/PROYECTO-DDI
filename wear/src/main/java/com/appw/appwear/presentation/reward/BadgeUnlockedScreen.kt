package com.appw.appwear.presentation.reward

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.appw.appwear.R
import com.appw.appwear.presentation.components.TintedIcon
import com.appw.appwear.presentation.components.color
import com.appw.appwear.presentation.components.iconRes
import com.appw.appwear.presentation.components.nameRes
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.shared.engine.Badge

/** Celebración de una insignia nueva que avisó el teléfono. */
@Composable
fun BadgeUnlockedScreen(badge: Badge, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val color = badge.color()
    val iconScale = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        iconScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    val glow by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1100), RepeatMode.Reverse),
        label = "glowAlpha",
    )

    ScreenScaffold(modifier = modifier) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(color.copy(alpha = glow), Color.Transparent))),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TintedIcon(
                iconRes = badge.iconRes(),
                color = color,
                contentDescription = null,
                size = 52.dp,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale.value
                    scaleY = iconScale.value
                },
            )
            val name = stringResource(badge.nameRes())
            val description = stringResource(R.string.badge_unlocked_description, name)
            Column(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clearAndSetSemantics { contentDescription = description },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.badge_unlocked_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                )
                Text(text = name, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            }
            CompactButton(
                onClick = onDone,
                modifier = Modifier.padding(top = 8.dp),
                label = { Text(stringResource(R.string.action_continue)) },
            )
        }
    }
}

@WearPreviewDevices
@Composable
private fun BadgeUnlockedScreenPreview() {
    LevelUpTheme {
        BadgeUnlockedScreen(badge = Badge.FIRST_STEP, onDone = {})
    }
}
