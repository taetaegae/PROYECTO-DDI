package com.appw.appwear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.LevelUpWearApp
import com.appw.appwear.data.WatchRepository
import com.appw.appwear.shared.model.CompletionReward
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado compartido por todas las pantallas del reloj. */
class WatchViewModel(private val repository: WatchRepository) : ViewModel() {

    val profile: StateFlow<PlayerProfile> = repository.profile
    val todayMissions: StateFlow<List<TodayMission>> = repository.todayMissions

    /** Recompensa de la última misión completada, para la pantalla R5. */
    private val _lastReward = MutableStateFlow<CompletionReward?>(null)
    val lastReward: StateFlow<CompletionReward?> = _lastReward.asStateFlow()

    fun completeMission(missionId: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            val reward = repository.completeMission(missionId) ?: return@launch
            _lastReward.value = reward
            onCompleted()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { WatchViewModel((this[APPLICATION_KEY] as LevelUpWearApp).repository) }
        }
    }
}
