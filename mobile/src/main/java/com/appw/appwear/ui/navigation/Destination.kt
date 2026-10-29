package com.appw.appwear.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.appw.appwear.R

/**
 * Pantallas del teléfono (T1 a T7 en el plan de implementación).
 * Las que tienen [labelRes] aparecen en la barra inferior.
 */
enum class Destination(
    val route: String,
    @get:StringRes val titleRes: Int,
    @get:StringRes val labelRes: Int? = null,
) {
    DASHBOARD("dashboard", R.string.app_name, R.string.nav_dashboard),
    MISSIONS("missions", R.string.title_missions, R.string.nav_missions),
    MISSION_EDITOR("missions/editor", R.string.title_mission_editor),
    STATS("stats", R.string.title_stats, R.string.nav_stats),
    BADGES("badges", R.string.title_badges, R.string.nav_badges),
    REWARDS("rewards", R.string.title_rewards, R.string.nav_rewards),
    SETTINGS("settings", R.string.title_settings);

    val isTopLevel: Boolean get() = labelRes != null

    companion object {
        val topLevel: List<Destination> = entries.filter { it.isTopLevel }

        fun fromRoute(route: String?): Destination = entries.find { it.route == route } ?: DASHBOARD
    }
}

@Composable
fun Destination.icon(): ImageVector = when (this) {
    Destination.MISSIONS -> Icons.AutoMirrored.Filled.List
    Destination.STATS -> ImageVector.vectorResource(R.drawable.ic_bar_chart)
    Destination.BADGES -> Icons.Filled.Star
    Destination.REWARDS -> Icons.Filled.ShoppingCart
    else -> Icons.Filled.Home
}
