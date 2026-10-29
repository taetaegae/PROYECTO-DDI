package com.appw.appwear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.appw.appwear.presentation.home.HomeScreen
import com.appw.appwear.presentation.missions.MissionsScreen
import com.appw.appwear.presentation.navigation.WearDestination
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
fun WearApp() {
    LevelUpTheme {
        AppScaffold {
            val navController = rememberSwipeDismissableNavController()
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = WearDestination.HOME,
            ) {
                composable(WearDestination.HOME) {
                    HomeScreen(onOpenMissions = { navController.navigate(WearDestination.MISSIONS) })
                }
                composable(WearDestination.MISSIONS) {
                    MissionsScreen()
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
