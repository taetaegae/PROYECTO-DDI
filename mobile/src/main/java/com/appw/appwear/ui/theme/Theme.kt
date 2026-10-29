package com.appw.appwear.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VioletLight,
    onPrimary = VioletDeep,
    primaryContainer = Violet,
    onPrimaryContainer = Color.White,
    secondary = XpGold,
    onSecondary = XpGoldDeep,
    tertiary = QuestMint,
    onTertiary = QuestMintDeep,
    background = Night,
    surface = Night,
    surfaceContainer = NightSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    primaryContainer = VioletContainerLight,
    onPrimaryContainer = VioletDeep,
    secondary = XpGoldDark,
    onSecondary = Color.White,
    secondaryContainer = XpGoldContainerLight,
    onSecondaryContainer = XpGoldDeep,
    tertiary = QuestMintDark,
    onTertiary = Color.White,
)

/** Tema de LevelUp. No usa colores dinámicos para conservar la identidad visual de la app. */
@Composable
fun LevelUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
