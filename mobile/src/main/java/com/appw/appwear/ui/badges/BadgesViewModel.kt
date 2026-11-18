package com.appw.appwear.ui.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.appContainer
import com.appw.appwear.data.repository.ProgressRepository
import com.appw.appwear.shared.engine.Badge
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BadgesViewModel(progressRepository: ProgressRepository) : ViewModel() {

    val unlocked: StateFlow<Set<Badge>> =
        progressRepository.badges.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { BadgesViewModel(appContainer().progressRepository) }
        }
    }
}
