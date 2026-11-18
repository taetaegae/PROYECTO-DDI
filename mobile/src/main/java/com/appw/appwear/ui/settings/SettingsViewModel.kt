package com.appw.appwear.ui.settings

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.R
import com.appw.appwear.appContainer
import com.appw.appwear.data.repository.MissionRepository
import com.appw.appwear.data.repository.ProgressRepository
import com.appw.appwear.data.settings.DemoSettings
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.sync.StatePublisher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Aviso que se muestra después de una acción del modo demo. */
sealed interface DemoMessage {
    data class LevelUpReady(val nextLevel: Int) : DemoMessage
    data object SamplesLoaded : DemoMessage
    data object SyncSent : DemoMessage
    data object ProgressReset : DemoMessage
}

data class SettingsUiState(
    /** El modo demo solo existe en la versión de depuración. */
    val isDebugBuild: Boolean,
    val demoMode: Boolean = false,
    val message: DemoMessage? = null,
)

/** Títulos de las misiones de ejemplo; vienen de strings.xml. */
data class SampleMissionTitles(val walk: String, val read: String, val draw: String)

class SettingsViewModel(
    private val demoSettings: DemoSettings,
    private val progressRepository: ProgressRepository,
    private val missionRepository: MissionRepository,
    private val statePublisher: StatePublisher,
    private val sampleTitles: SampleMissionTitles,
    isDebugBuild: Boolean,
) : ViewModel() {

    private val message = MutableStateFlow<DemoMessage?>(null)

    val uiState: StateFlow<SettingsUiState> =
        combine(demoSettings.demoMode, message) { demoMode, message ->
            SettingsUiState(isDebugBuild = isDebugBuild, demoMode = demoMode, message = message)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SettingsUiState(isDebugBuild = isDebugBuild, demoMode = demoSettings.demoMode.value),
        )

    fun setDemoMode(enabled: Boolean) {
        demoSettings.setDemoMode(enabled)
        message.value = null
    }

    fun prepareLevelUp() {
        viewModelScope.launch {
            message.value = DemoMessage.LevelUpReady(progressRepository.prepareDemoLevelUp())
        }
    }

    /** Crea o reemplaza tres misiones con ids fijos; si se cargan dos veces no se duplican. */
    fun loadSampleMissions() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            listOf(
                Mission("demo-walk", sampleTitles.walk, category = Category.EXERCISE, difficulty = Difficulty.EASY, recurrence = Recurrence.DAILY, createdAt = now),
                Mission("demo-read", sampleTitles.read, category = Category.HABITS, difficulty = Difficulty.EASY, recurrence = Recurrence.DAILY, createdAt = now),
                Mission("demo-draw", sampleTitles.draw, category = Category.CREATIVE, difficulty = Difficulty.HARD, recurrence = Recurrence.WEEKLY, createdAt = now),
            ).forEach { missionRepository.save(it) }
            message.value = DemoMessage.SamplesLoaded
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            progressRepository.resetDemoProgress()
            message.value = DemoMessage.ProgressReset
        }
    }

    fun forceSync() {
        viewModelScope.launch {
            statePublisher.forcePublish()
            message.value = DemoMessage.SyncSent
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                val container = appContainer()
                SettingsViewModel(
                    demoSettings = container.demoSettings,
                    progressRepository = container.progressRepository,
                    missionRepository = container.missionRepository,
                    statePublisher = container.statePublisher,
                    sampleTitles = SampleMissionTitles(
                        walk = app.getString(R.string.demo_mission_walk),
                        read = app.getString(R.string.demo_mission_read),
                        draw = app.getString(R.string.demo_mission_draw),
                    ),
                    isDebugBuild = app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
                )
            }
        }
    }
}
