@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.content.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.content.domain.states.ContentState
import com.ugg.admin.kit.content.domain.useCases.ContentUseCase
import com.ugg.admin.kit.core.data.formatting.toDayLabel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** The «Контент» tab: the sections and when each was last edited. */
class ContentViewModel(
    private val useCase: ContentUseCase,
    private val clock: Clock = Clock.System,
) : ViewModel() {
    val state: StateFlow<ContentState>
        field = MutableStateFlow(ContentState())

    // The labels are a nicety: without them the menu still works, so a failure only logs.
    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@ContentViewModel::class.toString() }

        state.update { it.copy(error = throwable) }
    }

    init {
        viewModelScope.launch(unexpectedExceptionHandler) {
            useCase.observeUpdatedAt().collect { updatedAt ->
                val now = clock.now().toEpochMilliseconds()
                state.update { it.copy(edited = updatedAt.mapValues { (_, at) -> "изменено ${at.toDayLabel(now)}" }, error = null) }
            }
        }
    }
}
