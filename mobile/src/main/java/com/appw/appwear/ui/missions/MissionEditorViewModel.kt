package com.appw.appwear.ui.missions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appw.appwear.appContainer
import com.appw.appwear.data.repository.MissionRepository
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.ui.navigation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/** Campos que el usuario edita en el formulario. */
data class MissionForm(
    val title: String = "",
    val description: String = "",
    val category: Category = Category.STUDY,
    val difficulty: Difficulty = Difficulty.EASY,
    val recurrence: Recurrence = Recurrence.DAILY,
)

enum class TitleError { REQUIRED, TOO_LONG }

data class MissionEditorUiState(
    val form: MissionForm = MissionForm(),
    val isEditing: Boolean = false,
    val titleError: TitleError? = null,
    /** true cuando se guardó o archivó la misión y hay que cerrar la pantalla. */
    val isFinished: Boolean = false,
)

class MissionEditorViewModel(
    private val missionRepository: MissionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val missionId: String? = savedStateHandle[Destination.ARG_MISSION_ID]

    // Misión original al editar; conserva los campos que el formulario no muestra.
    private var original: Mission? = null

    private val _uiState = MutableStateFlow(MissionEditorUiState(isEditing = missionId != null))
    val uiState: StateFlow<MissionEditorUiState> = _uiState.asStateFlow()

    init {
        if (missionId != null) {
            viewModelScope.launch {
                val mission = missionRepository.getMission(missionId) ?: return@launch
                original = mission
                _uiState.update {
                    it.copy(
                        form = MissionForm(
                            title = mission.title,
                            description = mission.description,
                            category = mission.category,
                            difficulty = mission.difficulty,
                            recurrence = mission.recurrence,
                        ),
                    )
                }
            }
        }
    }

    fun updateForm(transform: (MissionForm) -> MissionForm) {
        _uiState.update { it.copy(form = transform(it.form), titleError = null) }
    }

    fun save() {
        val form = _uiState.value.form
        val title = form.title.trim()
        val titleError = when {
            title.isEmpty() -> TitleError.REQUIRED
            title.length > MAX_TITLE_LENGTH -> TitleError.TOO_LONG
            else -> null
        }
        if (titleError != null) {
            _uiState.update { it.copy(titleError = titleError) }
            return
        }

        val base = original ?: Mission(
            id = UUID.randomUUID().toString(),
            title = title,
            category = form.category,
            difficulty = form.difficulty,
            recurrence = form.recurrence,
            createdAt = System.currentTimeMillis(),
        )
        val mission = base.copy(
            title = title,
            description = form.description.trim(),
            category = form.category,
            difficulty = form.difficulty,
            recurrence = form.recurrence,
        )
        viewModelScope.launch {
            missionRepository.save(mission)
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    fun archive() {
        val id = missionId ?: return
        viewModelScope.launch {
            missionRepository.archive(id)
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    companion object {
        const val MAX_TITLE_LENGTH = 60

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MissionEditorViewModel(appContainer().missionRepository, createSavedStateHandle())
            }
        }
    }
}
