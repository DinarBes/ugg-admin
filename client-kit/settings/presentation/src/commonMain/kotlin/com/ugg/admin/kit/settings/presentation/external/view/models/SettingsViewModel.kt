package com.ugg.admin.kit.settings.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.settings.domain.states.SettingsState
import com.ugg.admin.kit.settings.domain.useCases.SettingsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val useCase: SettingsUseCase,
) : ViewModel() {
    val state: StateFlow<SettingsState>
        field = MutableStateFlow(SettingsState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@SettingsViewModel::class.toString() }

        state.update { it.copy(error = throwable) }
    }

    init {
        viewModelScope.launch(unexpectedExceptionHandler) {
            useCase.observeAccount().collect { account ->
                // A null account mid sign-out keeps the last one on screen until the splash takes over.
                if (account != null) state.update { it.copy(account = account, error = null) }
            }
        }
    }

    /** The host signs out; this only keeps the button from being pressed twice. */
    fun onSignOutStarted() {
        state.update { it.copy(isSigningOut = true) }
    }
}
