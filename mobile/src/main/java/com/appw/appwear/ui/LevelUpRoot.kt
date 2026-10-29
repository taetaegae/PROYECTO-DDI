package com.appw.appwear.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appw.appwear.R
import com.appw.appwear.ui.badges.BadgesScreen
import com.appw.appwear.ui.dashboard.DashboardScreen
import com.appw.appwear.ui.missions.MissionEditorScreen
import com.appw.appwear.ui.missions.MissionsScreen
import com.appw.appwear.ui.navigation.Destination
import com.appw.appwear.ui.navigation.icon
import com.appw.appwear.ui.rewards.RewardsScreen
import com.appw.appwear.ui.settings.SettingsScreen
import com.appw.appwear.ui.stats.StatsScreen
import com.appw.appwear.ui.theme.LevelUpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = Destination.fromRoute(backStackEntry?.destination?.route)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(current.titleRes)) },
                navigationIcon = {
                    if (!current.isTopLevel) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    }
                },
                actions = {
                    if (current.isTopLevel) {
                        IconButton(onClick = { navController.navigate(Destination.SETTINGS.route) }) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.title_settings),
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (current.isTopLevel) {
                NavigationBar {
                    Destination.topLevel.forEach { destination ->
                        NavigationBarItem(
                            selected = destination == current,
                            onClick = { navController.navigateToTopLevel(destination) },
                            icon = { Icon(destination.icon(), contentDescription = null) },
                            label = { Text(stringResource(destination.labelRes!!)) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (current == Destination.MISSIONS) {
                FloatingActionButton(
                    onClick = { navController.navigate(Destination.MISSION_EDITOR.route) },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.title_mission_editor))
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.DASHBOARD.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Destination.DASHBOARD.route) { DashboardScreen() }
            composable(Destination.MISSIONS.route) { MissionsScreen() }
            composable(Destination.MISSION_EDITOR.route) { MissionEditorScreen() }
            composable(Destination.STATS.route) { StatsScreen() }
            composable(Destination.BADGES.route) { BadgesScreen() }
            composable(Destination.REWARDS.route) { RewardsScreen() }
            composable(Destination.SETTINGS.route) { SettingsScreen() }
        }
    }
}

/** Cambia de pestaña conservando el estado de cada una, sin apilar pantallas repetidas. */
private fun NavHostController.navigateToTopLevel(destination: Destination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
private fun LevelUpRootPreview() {
    LevelUpTheme {
        LevelUpRoot()
    }
}
