package com.ugg.admin.kit.orders.data.useCases

import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.orders.domain.exceptions.OrderNotFoundException
import com.ugg.admin.kit.orders.domain.useCases.OrdersUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Orders kept in memory, so stage changes and notes survive moving between the list and the
 * order card — but not a restart. Bind one instance for the whole app.
 */
class MockOrdersUseCase(
    seed: List<Order> = Order.mocks,
    private val latency: Duration = 300.milliseconds,
) : OrdersUseCase {
    private val orders = MutableStateFlow(seed)

    override fun observeOrders(): Flow<List<Order>> = orders.map { list -> list.sortedByDescending { it.number } }

    override fun observeOrder(orderId: String): Flow<Order> = orders
        .map { list -> list.firstOrNull { it.id == orderId } ?: throw OrderNotFoundException() }
        .distinctUntilChanged()

    override suspend fun advance(orderId: String) = change(orderId) { order ->
        order.copy(status = order.status.next ?: order.status)
    }

    override suspend fun saveDetails(orderId: String, readyAt: Long?, note: String) = change(orderId) { order ->
        order.copy(readyAt = readyAt, note = note.trim())
    }

    override suspend fun cancel(orderId: String) = change(orderId) { order ->
        if (order.status.isActive) order.copy(status = OrderStatus.Cancelled) else order
    }

    private suspend fun change(orderId: String, transform: (Order) -> Order) {
        delay(latency)
        if (orders.value.none { it.id == orderId }) throw OrderNotFoundException()

        orders.update { list -> list.map { if (it.id == orderId) transform(it) else it } }
    }
}
