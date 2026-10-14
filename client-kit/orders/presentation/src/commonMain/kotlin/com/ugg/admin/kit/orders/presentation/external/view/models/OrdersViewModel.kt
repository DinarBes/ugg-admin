package com.ugg.admin.kit.orders.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.core.data.formatting.toDayAndMonth
import com.ugg.admin.kit.orders.domain.entities.OrderFilter
import com.ugg.admin.kit.orders.domain.states.OrdersState
import com.ugg.admin.kit.orders.domain.useCases.OrdersUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class OrdersViewModel(
    private val useCase: OrdersUseCase,
) : ViewModel() {
    val state: StateFlow<OrdersState>
        field = MutableStateFlow(OrdersState(today = todayLabel()))

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@OrdersViewModel::class.toString() }

        state.update { it.copy(error = throwable, isLoading = false) }
    }

    private var observing: Job? = null

    init {
        fetch()
    }

    /** Subscribes to the live list; called again by «Повторить» after a failure. */
    fun fetch() {
        observing?.cancel()
        state.update { it.copy(isLoading = true, error = null, today = todayLabel()) }

        observing = viewModelScope.launch(unexpectedExceptionHandler) {
            useCase.observeOrders().collect { orders ->
                state.update { it.copy(orders = orders, isLoading = false, error = null) }
            }
        }
    }

    fun onFilter(filter: OrderFilter) {
        state.update { it.copy(filter = filter, isSearching = false, query = "") }
    }

    fun onToggleSearch() {
        state.update { it.copy(isSearching = !it.isSearching, query = "") }
    }

    fun onQuery(query: String) {
        state.update { it.copy(query = query) }
    }

    @OptIn(ExperimentalTime::class)
    private fun todayLabel() = "Сегодня, ${Clock.System.now().toEpochMilliseconds().toDayAndMonth()}"
}
