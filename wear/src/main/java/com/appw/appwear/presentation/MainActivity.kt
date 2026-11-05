package com.appw.appwear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.appw.appwear.presentation.home.HomeScreen
import com.appw.appwear.presentation.missions.MissionDetailScreen
import com.appw.appwear.presentation.missions.MissionsScreen
import com.appw.appwear.presentation.navigation.WearDestination
import com.appw.appwear.presentation.reward.MissionDoneScreen
import com.appw.appwear.presentation.theme.LevelUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp(viewModel: WatchViewModel = viewModel(factory = WatchViewModel.Factory)) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val missions by viewModel.todayMissions.collectAsStateWithLifecycle()
    val lastReward by viewModel.lastReward.collectAsStateWithLifecycle()

    LevelUpTheme {
        AppScaffold {
            val navController = rememberSwipeDismissableNavController()
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = WearDestination.HOME,
            ) {
                composable(WearDestination.HOME) {
                    HomeScreen(
                        profile = profile,
                        onOpenMissions = { navController.navigate(WearDestination.MISSIONS) },
                    )
                }
                composable(WearDestination.MISSIONS) {
                    MissionsScreen(
                        missions = missions,
                        onMissionClick = { missionId ->
                            navController.navigate(WearDestination.missionDetail(missionId))
                        },
                    )
                }
                composable(WearDestination.MISSION_DETAIL) { entry ->
                    val missionId = entry.arguments?.getString(WearDestination.ARG_MISSION_ID)
                    MissionDetailScreen(
                        item = missions.find { it.mission.id == missionId },
                        onComplete = {
                            if (missionId != null) {
                                viewModel.completeMission(missionId) {
                                    // Al volver desde R5 se regresa a la lista, no al detalle.
                                    navController.navigate(WearDestination.MISSION_DONE) {
                                        popUpTo(WearDestination.MISSIONS)
                                    }
                                }
                            }
                        },
                    )
                }
                composable(WearDestination.MISSION_DONE) {
                    MissionDoneScreen(reward = lastReward, onDone = { navController.popBackStack() })
                }
            }
        }
    }
}
