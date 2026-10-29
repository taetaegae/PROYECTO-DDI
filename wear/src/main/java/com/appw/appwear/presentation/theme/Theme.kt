package com.appw.appwear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val LevelUpColorScheme = ColorScheme(
    primary = VioletLight,
    onPrimary = VioletDeep,
    primaryContainer = Violet,
    onPrimaryContainer = Color.White,
    secondary = XpGold,
    onSecondary = XpGoldDeep,
    tertiary = QuestMint,
    onTertiary = QuestMintDeep,
)

@Composable
fun LevelUpTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LevelUpColorScheme,
        content = content
    )
}
