package com.appw.appwear.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.appw.appwear.feedback.Haptics
import com.appw.appwear.notify.BadgeInbox
import androidx.navigation.NavController
import com.appw.appwear.presentation.home.HomeScreen
import com.appw.appwear.presentation.missions.MissionDetailScreen
import com.appw.appwear.presentation.missions.MissionsScreen
import com.appw.appwear.presentation.navigation.WearDestination
import com.appw.appwear.presentation.reward.BadgeUnlockedScreen
import com.appw.appwear.presentation.reward.LevelUpScreen
import com.appw.appwear.presentation.reward.MissionDoneScreen
import com.appw.appwear.presentation.theme.LevelUpTheme
import com.appw.appwear.presentation.timer.TimerScreen

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
    val timer by viewModel.timer.collectAsStateWithLifecycle()
    val timerResult by viewModel.timerResult.collectAsStateWithLifecycle()
    val demoMode by viewModel.demoMode.collectAsStateWithLifecycle()
    val pendingBadges by BadgeInbox.pending.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // El temporizador funciona aunque se niegue el permiso; solo no se ve su notificación.
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LevelUpTheme {
        AppScaffold {
            val navController = rememberSwipeDismissableNavController()

            // Al abrir la app con un temporizador corriendo, se muestra directamente R4.
            LaunchedEffect(Unit) {
                if (viewModel.timer.value != null && navController.currentDestination?.route != WearDestination.TIMER) {
                    navController.navigate(WearDestination.TIMER)
                }
            }

            // Ruta actual, para no interrumpir el temporizador ni otra celebración.
            var currentRoute by remember { mutableStateOf<String?>(null) }
            DisposableEffect(navController) {
                val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                    currentRoute = destination.route
                }
                navController.addOnDestinationChangedListener(listener)
                onDispose { navController.removeOnDestinationChangedListener(listener) }
            }

            // Insignia nueva avisada por el teléfono: se celebra al volver a una pantalla tranquila,
            // por ejemplo después de R5 y R6.
            val nextBadge = pendingBadges.firstOrNull()
            LaunchedEffect(nextBadge, currentRoute) {
                if (nextBadge != null && currentRoute in CALM_ROUTES) {
                    navController.navigate(WearDestination.BADGE)
                }
            }

            // Cuando el temporizador termina, se vuelve a la lista y se muestra R5 con la recompensa.
            LaunchedEffect(timerResult) {
                val result = timerResult ?: return@LaunchedEffect
                viewModel.acknowledgeTimerResult(result)
                navController.navigate(WearDestination.MISSIONS) { popUpTo(WearDestination.HOME) }
                if (result.reward != null) navController.navigate(WearDestination.MISSION_DONE)
            }
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
                    val item = missions.find { it.mission.id == missionId }
                    MissionDetailScreen(
                        item = item,
                        isTimerRunning = timer != null && timer?.missionId == missionId,
                        demoMode = demoMode,
                        onStartTimer = {
                            if (item != null && timer?.missionId != missionId) {
                                viewModel.startTimer(item.mission)
                                if (needsNotificationPermission(context)) {
                                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                            navController.navigate(WearDestination.TIMER)
                        },
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
                composable(WearDestination.TIMER) {
                    TimerScreen(
                        state = timer,
                        onPause = viewModel::pauseTimer,
                        onResume = viewModel::resumeTimer,
                        onCancel = {
                            viewModel.cancelTimer()
                            navController.popBackStack()
                        },
                    )
                }
                composable(WearDestination.MISSION_DONE) {
                    LaunchedEffect(Unit) { Haptics.missionDone(context) }
                    MissionDoneScreen(
                        reward = lastReward,
                        onDone = {
                            // Si subió de nivel, R6 reemplaza a R5; al salir de R6 se vuelve a la lista.
                            if (lastReward?.leveledUp == true) {
                                navController.navigate(WearDestination.LEVEL_UP) {
                                    popUpTo(WearDestination.MISSIONS)
                                }
                            } else {
                                navController.popBackStack()
                            }
                        },
                    )
                }
                composable(WearDestination.BADGE) {
                    // Se fija al entrar, para que no cambie mientras la pantalla se cierra.
                    val badge = remember { BadgeInbox.pending.value.firstOrNull() }
                    LaunchedEffect(Unit) { Haptics.badgeUnlocked(context) }
                    if (badge != null) {
                        BadgeUnlockedScreen(
                            badge = badge,
                            onDone = {
                                BadgeInbox.consume(badge)
                                navController.popBackStack()
                            },
                        )
                    }
                }
                composable(WearDestination.LEVEL_UP) {
                    LaunchedEffect(Unit) { Haptics.levelUp(context) }
                    LevelUpScreen(
                        level = lastReward?.levelAfter ?: profile.progress.level,
                        onDone = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

/** POST_NOTIFICATIONS se pide en tiempo de ejecución desde Android 13. */
private fun needsNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

/** Pantallas donde se puede mostrar la celebración de una insignia sin interrumpir nada. */
private val CALM_ROUTES = setOf(WearDestination.HOME, WearDestination.MISSIONS, WearDestination.MISSION_DETAIL)
