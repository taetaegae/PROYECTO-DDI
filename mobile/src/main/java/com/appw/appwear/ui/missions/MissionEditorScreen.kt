package com.appw.appwear.ui.missions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appw.appwear.R
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.ui.components.labelRes
import com.appw.appwear.ui.theme.LevelUpTheme

/** T3 · Crear o editar misión. */
@Composable
fun MissionEditorScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MissionEditorViewModel = viewModel(factory = MissionEditorViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) onDone()
    }

    MissionEditorContent(
        uiState = uiState,
        onFormChange = viewModel::updateForm,
        onSave = viewModel::save,
        onArchive = viewModel::archive,
        modifier = modifier,
    )
}

@Composable
fun MissionEditorContent(
    uiState: MissionEditorUiState,
    onFormChange: ((MissionForm) -> MissionForm) -> Unit,
    onSave: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val form = uiState.form
    val titleError = when (uiState.titleError) {
        TitleError.REQUIRED -> stringResource(R.string.error_title_required)
        TitleError.TOO_LONG -> stringResource(R.string.error_title_too_long, MissionEditorViewModel.MAX_TITLE_LENGTH)
        null -> null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = form.title,
            onValueChange = { value -> onFormChange { it.copy(title = value) } },
            label = { Text(stringResource(R.string.field_title)) },
            isError = titleError != null,
            supportingText = { if (titleError != null) Text(titleError) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = form.description,
            onValueChange = { value -> onFormChange { it.copy(description = value) } },
            label = { Text(stringResource(R.string.field_description)) },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        ChoiceChips(
            label = stringResource(R.string.field_category),
            options = Category.entries,
            selected = form.category,
            onSelect = { category -> onFormChange { it.copy(category = category) } },
            optionLabel = { stringResource(it.labelRes()) },
        )
        ChoiceChips(
            label = stringResource(R.string.field_difficulty),
            options = Difficulty.entries,
            selected = form.difficulty,
            onSelect = { difficulty -> onFormChange { it.copy(difficulty = difficulty) } },
            optionLabel = { stringResource(R.string.difficulty_option_format, stringResource(it.labelRes()), it.baseXp) },
        )
        ChoiceChips(
            label = stringResource(R.string.field_recurrence),
            options = Recurrence.entries,
            selected = form.recurrence,
            onSelect = { recurrence -> onFormChange { it.copy(recurrence = recurrence) } },
            optionLabel = { stringResource(it.labelRes()) },
        )
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.action_save))
        }
        if (uiState.isEditing) {
            OutlinedButton(onClick = onArchive, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.action_archive))
            }
        }
    }
}

/** Grupo de chips donde solo una opción puede estar seleccionada. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> ChoiceChips(
    label: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    optionLabel: @Composable (T) -> String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(optionLabel(option)) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MissionEditorContentPreview() {
    LevelUpTheme {
        MissionEditorContent(
            uiState = MissionEditorUiState(),
            onFormChange = {},
            onSave = {},
            onArchive = {},
        )
    }
}
