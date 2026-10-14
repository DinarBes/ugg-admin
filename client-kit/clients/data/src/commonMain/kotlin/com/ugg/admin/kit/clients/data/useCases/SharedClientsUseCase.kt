package com.ugg.admin.kit.clients.data.useCases

import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderStatus
import com.ugg.admin.infrastructure.firebase.firestore.dto.UserDto
import com.ugg.admin.infrastructure.firebase.firestore.services.OrderService
import com.ugg.admin.infrastructure.firebase.firestore.services.UserService
import com.ugg.admin.kit.clients.domain.entities.Client
import com.ugg.admin.kit.clients.domain.entities.ClientOrder
import com.ugg.admin.kit.clients.domain.useCases.ClientsUseCase
import com.ugg.admin.kit.core.data.content.translatingContentErrors
import com.ugg.admin.kit.core.data.content.whileSignedIn
import com.ugg.admin.kit.core.data.formatting.formatPhone
import com.ugg.admin.kit.core.data.formatting.toDayLabel
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** `users` in Firestore with their orders counted; both live. */
class SharedClientsUseCase(
    private val phoneAuth: PhoneAuthService,
    private val users: UserService,
    private val orders: OrderService,
    private val clock: Clock = Clock.System,
) : ClientsUseCase {

    override fun observeClients(): Flow<List<Client>> = phoneAuth.whileSignedIn(emptyList()) {
        combine(users.observeUsers(), orders.observeOrders()) { profiles, allOrders ->
            val byUser = allOrders.groupBy { it.userId }
            val now = clock.now().toEpochMilliseconds()
            profiles.map { it.toClient(byUser[it.id].orEmpty(), now) }
        }
    }.translatingContentErrors()
}

/** [orders] newest first, as the order service returns them. */
internal fun UserDto.toClient(orders: List<OrderDto>, now: Long): Client {
    val active = orders.firstOrNull { it.status.isActive }

    return Client(
        id = id,
        name = name.takeIf { it.isNotBlank() },
        phone = phone.formatPhone(),
        phoneDial = phone,
        size = size?.let { size ->
            listOf(size.label, heightName(size.heightId)).filter { it.isNotBlank() }.joinToString(" ").takeIf { it.isNotBlank() }
        },
        ordersCount = orders.size,
        activeOrder = active?.let { ClientOrder(orderId = it.id, number = it.number, status = it.status.label) },
        lastOrderId = orders.firstOrNull()?.id,
        registeredAt = createdAt?.toDayLabel(now).orEmpty(),
    )
}

private val OrderStatus.label: String
    get() = when (this) {
        OrderStatus.Accepted -> "новый"
        OrderStatus.Cutting -> "раскрой"
        OrderStatus.Sewing -> "пошив"
        OrderStatus.Ready -> "готов"
        OrderStatus.Completed -> "выдан"
        OrderStatus.Cancelled -> "отменён"
    }

private fun heightName(id: String): String = when (id) {
    "standard" -> "стандарт"
    "tall" -> "высокие"
    else -> id
}
