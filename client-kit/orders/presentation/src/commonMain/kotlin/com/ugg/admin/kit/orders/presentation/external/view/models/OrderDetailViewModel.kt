package com.ugg.admin.kit.orders.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.orders.domain.states.OrderDetailState
import com.ugg.admin.kit.orders.domain.useCases.OrdersUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The order card. ViewModels are shared per type, so the screen calls [load] with its order on
 * every visit instead of the ViewModel loading itself in `init`.
 */
class OrderDetailViewModel(
    private val useCase: OrdersUseCase,
) : ViewModel() {
    val state: StateFlow<OrderDetailState>
        field = MutableStateFlow(OrderDetailState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@OrderDetailViewModel::class.toString() }

        state.update { it.copy(error = throwable, isLoading = false, isUpdating = false, isSaving = false) }
    }

    private var observing: Job? = null
    private var orderId: String? = null

    fun load(orderId: String) {
        this.orderId = orderId
        observing?.cancel()
        state.update { OrderDetailState(isLoading = true) }

        observing = viewModelScope.launch(unexpectedExceptionHandler) {
            useCase.observeOrder(orderId).collect { order ->
                state.update { current ->
                    // Fresh data overwrites the form only while the master has not touched it.
                    val keepEdits = current.order != null && current.hasChanges
                    current.copy(
                        order = order,
                        readyAt = if (keepEdits) current.readyAt else order.readyAt,
                        note = if (keepEdits) current.note else order.note,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun retry() {
        orderId?.let(::load)
    }

    fun onReadyAtChange(readyAt: Long?) {
        state.update { it.copy(readyAt = readyAt) }
    }

    fun onNoteChange(note: String) {
        state.update { it.copy(note = note) }
    }

    fun onAdvance() = perform { useCase.advance(it) }

    fun onCancel() = perform { useCase.cancel(it) }

    fun onSave() {
        val id = orderId ?: return
        val current = state.value
        if (!current.hasChanges || current.isSaving) return

        viewModelScope.launch(unexpectedExceptionHandler) {
            state.update { it.copy(isSaving = true, error = null) }
            try {
                useCase.saveDetails(id, current.readyAt, current.note)
                // The flow brings the saved order back; take it as the new baseline for the form.
                state.update { it.copy(order = it.order?.copy(readyAt = current.readyAt, note = current.note.trim()), note = current.note.trim()) }
            } finally {
                state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun perform(action: suspend (String) -> Unit) {
        val id = orderId ?: return
        if (state.value.isUpdating) return

        viewModelScope.launch(unexpectedExceptionHandler) {
            state.update { it.copy(isUpdating = true, error = null) }
            try {
                action(id)
            } finally {
                state.update { it.copy(isUpdating = false) }
            }
        }
    }
}
