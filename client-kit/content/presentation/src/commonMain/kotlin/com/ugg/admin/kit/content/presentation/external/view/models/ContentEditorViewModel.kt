package com.ugg.admin.kit.content.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.content.domain.entities.ContentDraft
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.domain.states.ContentEditorState
import com.ugg.admin.kit.content.domain.useCases.ContentUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * One editor for every section: the draft is whichever [ContentDraft] the section has. Shared per
 * type like every ViewModel here, so the screen calls [load] on each visit.
 */
class ContentEditorViewModel(
    private val useCase: ContentUseCase,
) : ViewModel() {
    val state: StateFlow<ContentEditorState>
        field = MutableStateFlow(ContentEditorState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@ContentEditorViewModel::class.toString() }

        state.update { it.copy(error = throwable, isLoading = false, isSaving = false) }
    }

    private var loading: Job? = null

    fun load(section: ContentSection) {
        loading?.cancel()
        state.update { ContentEditorState(section = section, isLoading = true) }

        loading = viewModelScope.launch(unexpectedExceptionHandler) {
            val content = useCase.fetch(section)
            state.update { it.copy(saved = content, draft = content, isLoading = false) }
        }
    }

    fun retry() {
        state.value.section?.let(::load)
    }

    /** Every keystroke of every editor lands here with the whole updated section. */
    fun onChange(draft: ContentDraft) {
        state.update { it.copy(draft = draft, isJustSaved = false, error = null) }
    }

    fun onSave() {
        val draft = state.value.draft ?: return
        if (!state.value.canSave) return

        viewModelScope.launch(unexpectedExceptionHandler) {
            state.update { it.copy(isSaving = true, error = null) }
            try {
                useCase.save(draft)
                state.update { it.copy(saved = draft, isJustSaved = true) }
            } finally {
                state.update { it.copy(isSaving = false) }
            }
        }
    }
}
