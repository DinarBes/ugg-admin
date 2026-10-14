package com.ugg.admin.kit.orders.domain.useCases

import com.ugg.admin.kit.orders.domain.entities.Order
import kotlinx.coroutines.flow.Flow

/**
 * Orders as the workshop manages them. Both reads are live: a change made on the order card shows
 * up in the list without a reload, and so will a new order from a customer.
 */
interface OrdersUseCase {
    /** Every order, newest first. */
    fun observeOrders(): Flow<List<Order>>

    /** Throws [com.ugg.admin.kit.orders.domain.exceptions.OrderNotFoundException] into the flow. */
    fun observeOrder(orderId: String): Flow<Order>

    /** Moves the order to [com.ugg.admin.kit.orders.domain.entities.OrderStatus.next]. */
    suspend fun advance(orderId: String)

    /** [readyAt] is epoch milliseconds (UTC midnight of the chosen day) or `null` for «не назначена». */
    suspend fun saveDetails(orderId: String, readyAt: Long?, note: String)

    suspend fun cancel(orderId: String)
}
