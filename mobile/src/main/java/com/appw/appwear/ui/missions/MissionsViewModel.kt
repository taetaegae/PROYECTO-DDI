package com.appw.appwear.ui.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.appContainer
import com.appw.appwear.data.repository.MissionRepository
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MissionsUiState(
    val missions: List<Mission> = emptyList(),
    /** null significa "todas las categorías". */
    val selectedCategory: Category? = null,
    val isLoading: Boolean = true,
)

class MissionsViewModel(missionRepository: MissionRepository) : ViewModel() {

    private val selectedCategory = MutableStateFlow<Category?>(null)

    val uiState: StateFlow<MissionsUiState> =
        combine(missionRepository.activeMissions, selectedCategory) { missions, category ->
            MissionsUiState(
                missions = missions.filter { category == null || it.category == category },
                selectedCategory = category,
                isLoading = false,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MissionsUiState())

    fun selectCategory(category: Category?) {
        selectedCategory.value = category
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MissionsViewModel(appContainer().missionRepository) }
        }
    }
}
