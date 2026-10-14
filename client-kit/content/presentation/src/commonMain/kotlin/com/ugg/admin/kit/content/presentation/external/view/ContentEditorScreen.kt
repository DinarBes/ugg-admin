package com.ugg.admin.kit.content.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.presentation.external.view.models.ContentEditorViewModel
import com.ugg.admin.kit.content.presentation.internal.view.ContentEditorView
import com.ugg.admin.kit.core.presentation.external.components.UggAlertDialog
import org.kodein.di.compose.viewmodel.rememberViewModel

/** The texts of one section. Leaving with unsaved edits asks first. */
@Composable
fun ContentEditorScreen(section: ContentSection, onBack: () -> Unit) {
    val viewModel: ContentEditorViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()
    var isConfirmingLeave by rememberSaveable { mutableStateOf(false) }

    // A fresh form on every visit, but not on rotation: the flag survives it, a new visit starts without it.
    var isStarted by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(section) {
        if (!isStarted) {
            isStarted = true
            viewModel.load(section)
        }
    }

    ContentEditorView(
        state = state,
        onBack = { if (state.hasChanges) isConfirmingLeave = true else onBack() },
        onRetry = viewModel::retry,
        onChange = viewModel::onChange,
        onSave = viewModel::onSave,
    )

    if (isConfirmingLeave) {
        UggAlertDialog(
            title = "Выйти без сохранения?",
            text = "Правки в этом разделе пропадут.",
            confirmText = "Выйти",
            dismissText = "Остаться",
            onConfirm = {
                isConfirmingLeave = false
                onBack()
            },
            onDismiss = { isConfirmingLeave = false },
        )
    }
}
