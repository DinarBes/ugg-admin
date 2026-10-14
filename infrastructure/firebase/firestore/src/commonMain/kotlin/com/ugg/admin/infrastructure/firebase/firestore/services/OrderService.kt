package com.ugg.admin.infrastructure.firebase.firestore.services

import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderStatus
import com.ugg.admin.infrastructure.firebase.firestore.internal.DocumentStore
import com.ugg.admin.infrastructure.firebase.firestore.internal.RawQuery
import com.ugg.admin.infrastructure.firebase.firestore.internal.ServerTimestamp
import com.ugg.admin.infrastructure.firebase.firestore.internal.decode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * `orders/{id}` from the workshop's side: every customer's orders, stage changes and deadlines.
 * The master's note lives apart, in `orderNotes/{orderId}`: the customer may read their order
 * document, so anything private can't sit in it.
 *
 * Needs `isAdmin: true` in the signed-in user's profile; without it the rules answer PermissionDenied.
 */
interface OrderService {
    /** All orders, newest first. */
    fun observeOrders(): Flow<List<OrderDto>>

    /** `null` once the order is deleted. */
    fun observeOrder(orderId: String): Flow<OrderDto?>

    /** The master's note; empty when there is none. */
    fun observeNote(orderId: String): Flow<String>

    suspend fun setStatus(orderId: String, status: OrderStatus)

    /** [readyAt] — epoch milliseconds, or `null` to clear the date. The customer sees it in «Мой заказ». */
    suspend fun setReadyAt(orderId: String, readyAt: Long?)

    suspend fun saveNote(orderId: String, note: String)
}

internal class FirestoreOrderService(
    private val store: DocumentStore,
) : OrderService {

    override fun observeOrders(): Flow<List<OrderDto>> =
        store.observe(RawQuery(collection = ORDERS, orderBy = "createdAt", descending = true))
            .map { documents -> documents.map { it.decode(OrderDto.serializer()) } }

    override fun observeOrder(orderId: String): Flow<OrderDto?> =
        store.observe("$ORDERS/$orderId").map { it?.decode(OrderDto.serializer()) }

    override fun observeNote(orderId: String): Flow<String> =
        store.observe("$NOTES/$orderId").map { it?.data?.get(NOTE_FIELD) as? String ?: "" }

    override suspend fun setStatus(orderId: String, status: OrderStatus) =
        update(orderId, mapOf("status" to status.serialName))

    override suspend fun setReadyAt(orderId: String, readyAt: Long?) =
        update(orderId, mapOf("estimatedReadyAt" to readyAt))

    override suspend fun saveNote(orderId: String, note: String) =
        store.set("$NOTES/$orderId", mapOf(NOTE_FIELD to note, "updatedAt" to ServerTimestamp), merge = true)

    private suspend fun update(orderId: String, fields: Map<String, Any?>) =
        store.set("$ORDERS/$orderId", fields + ("updatedAt" to ServerTimestamp), merge = true)

    private companion object {
        const val ORDERS = "orders"
        const val NOTES = "orderNotes"
        const val NOTE_FIELD = "text"
    }
}

/** Exactly how the status is spelled in Firestore (the `@SerialName`). */
internal val OrderStatus.serialName: String
    get() = name.lowercase()
