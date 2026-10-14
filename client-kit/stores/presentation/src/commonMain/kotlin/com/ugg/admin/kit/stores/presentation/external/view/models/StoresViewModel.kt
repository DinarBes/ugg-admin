package com.ugg.admin.kit.stores.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.stores.domain.states.StoresState
import com.ugg.admin.kit.stores.domain.useCases.StoresUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoresViewModel(
    private val useCase: StoresUseCase,
) : ViewModel() {
    val state: StateFlow<StoresState>
        field = MutableStateFlow(StoresState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@StoresViewModel::class.toString() }

        state.update { it.copy(error = throwable, isLoading = false) }
    }

    private var observing: Job? = null

    init {
        fetch()
    }

    fun fetch() {
        observing?.cancel()
        state.update { it.copy(isLoading = true, error = null) }

        observing = viewModelScope.launch(unexpectedExceptionHandler) {
            useCase.observeStores().collect { stores ->
                state.update { it.copy(stores = stores, isLoading = false, error = null) }
            }
        }
    }
}
