package com.appw.appwear.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.appContainer
import com.appw.appwear.data.repository.CompletionOutcome
import com.appw.appwear.data.repository.ProgressRepository
import com.appw.appwear.shared.model.CompletionSource
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val profile: PlayerProfile = PlayerProfile(),
    val todayMissions: List<TodayMission> = emptyList(),
    val isLoading: Boolean = true,
)

class DashboardViewModel(private val progressRepository: ProgressRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        combine(progressRepository.profile, progressRepository.todayMissions) { profile, missions ->
            DashboardUiState(profile = profile, todayMissions = missions, isLoading = false)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    // Un Channel entrega cada resultado una sola vez, aunque la pantalla se recomponga.
    private val outcomes = Channel<CompletionOutcome>(Channel.BUFFERED)
    val completionOutcomes: Flow<CompletionOutcome> = outcomes.receiveAsFlow()

    fun completeMission(missionId: String) {
        viewModelScope.launch {
            outcomes.send(progressRepository.completeMission(missionId, CompletionSource.PHONE))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { DashboardViewModel(appContainer().progressRepository) }
        }
    }
}
