package com.ugg.admin.kit.orders.data.useCases

import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import com.ugg.admin.infrastructure.firebase.firestore.services.OrderService
import com.ugg.admin.infrastructure.firebase.firestore.services.UserService
import com.ugg.admin.kit.core.data.content.translatingContentErrors
import com.ugg.admin.kit.core.data.content.whileSignedIn
import com.ugg.admin.kit.orders.data.mappers.toDomain
import com.ugg.admin.kit.orders.data.mappers.toDto
import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.orders.domain.exceptions.OrderNotFoundException
import com.ugg.admin.kit.orders.domain.useCases.OrdersUseCase
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * Orders from Firestore, joined with the customers' profiles (`users`) for names, phones and
 * sizes. Both lists are live, so a new order or a stage changed on another phone shows up at once.
 */
class SharedOrdersUseCase(
    private val phoneAuth: PhoneAuthService,
    private val orders: OrderService,
    private val users: UserService,
    private val clock: Clock = Clock.System,
) : OrdersUseCase {

    override fun observeOrders(): Flow<List<Order>> = phoneAuth.whileSignedIn(emptyList()) {
        combine(orders.observeOrders(), users.observeUsers()) { list, profiles ->
            val byId = profiles.associateBy { it.id }
            val now = clock.now().toEpochMilliseconds()
            list.map { it.toDomain(byId[it.userId], note = "", now = now) }
        }
    }.translatingContentErrors()

    override fun observeOrder(orderId: String): Flow<Order> =
        combine(orders.observeOrder(orderId), orders.observeNote(orderId), users.observeUsers()) { order, note, profiles ->
            order ?: throw OrderNotFoundException()
            order.toDomain(profiles.firstOrNull { it.id == order.userId }, note, clock.now().toEpochMilliseconds())
        }.translatingContentErrors()

    override suspend fun advance(orderId: String) = translatingContentErrors {
        val next = current(orderId).status.next ?: return@translatingContentErrors
        orders.setStatus(orderId, next.toDto())
    }

    override suspend fun saveDetails(orderId: String, readyAt: Long?, note: String) = translatingContentErrors {
        val order = current(orderId)
        if (order.readyAt != readyAt) orders.setReadyAt(orderId, readyAt)
        if (order.note != note.trim()) orders.saveNote(orderId, note.trim())
    }

    override suspend fun cancel(orderId: String) = translatingContentErrors {
        if (current(orderId).status.isActive) orders.setStatus(orderId, OrderStatus.Cancelled.toDto())
    }

    /** The order as it is now, note included — the base for a change. */
    private suspend fun current(orderId: String): Order = observeOrder(orderId).first()
}
