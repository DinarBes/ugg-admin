package com.ugg.admin.kit.clients.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.clients.domain.states.ClientsState
import com.ugg.admin.kit.clients.domain.useCases.ClientsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientsViewModel(
    private val useCase: ClientsUseCase,
) : ViewModel() {
    val state: StateFlow<ClientsState>
        field = MutableStateFlow(ClientsState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@ClientsViewModel::class.toString() }

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
            useCase.observeClients().collect { clients ->
                state.update { it.copy(clients = clients, isLoading = false, error = null) }
            }
        }
    }

    fun onQuery(query: String) {
        state.update { it.copy(query = query) }
    }
}
