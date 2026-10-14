package com.ugg.admin.kit.orders.data.mappers

import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderDeliveryDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderItemDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderStatus as OrderStatusDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.UserDto
import com.ugg.admin.kit.core.data.formatting.formatPhone
import com.ugg.admin.kit.core.data.formatting.rubles
import com.ugg.admin.kit.core.data.formatting.toArgb
import com.ugg.admin.kit.core.data.formatting.toMomentLabel
import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderBoots
import com.ugg.admin.kit.orders.domain.entities.OrderCustomer
import com.ugg.admin.kit.orders.domain.entities.OrderDelivery
import com.ugg.admin.kit.orders.domain.entities.OrderLine
import com.ugg.admin.kit.orders.domain.entities.OrderStatus

/**
 * An order with its customer ([user] is `null` when the profile was deleted — orders outlive it)
 * and the master's private [note]. [now] decides «сегодня» / «вчера».
 */
internal fun OrderDto.toDomain(user: UserDto?, note: String, now: Long): Order {
    val boots = items.firstOrNull { it.kind == OrderItemDto.KIND_BOOTS }
    val extras = items.filter { it.kind == OrderItemDto.KIND_EXTRA }
    val bootsOptions = boots?.options().orEmpty()

    return Order(
        id = id,
        number = number,
        status = status.toDomain(),
        createdAt = createdAt?.toMomentLabel(now).orEmpty(),
        customer = customer(user),
        boots = boots?.let {
            OrderBoots(
                title = it.title,
                quantity = it.quantity,
                options = bootsOptions,
                color = it.color?.toArgb() ?: DEFAULT_SHADE,
                price = (it.unitPrice * it.quantity).rubles(),
            )
        },
        extras = buildList {
            extras.forEach { add(OrderLine("${it.title} × ${it.quantity}", (it.unitPrice * it.quantity).rubles())) }
            if (giftWrap) add(OrderLine("Подарочная упаковка", "бесплатно"))
            if (delivery.price > 0) add(OrderLine(delivery.title, delivery.price.rubles()))
        },
        itemsSummary = (listOfNotNull(bootsOptions.ifEmpty { null }) + extras.map { it.title.replaceFirstChar(Char::lowercaseChar) })
            .joinToString(" + "),
        delivery = delivery.toDomain(),
        total = totals.total.rubles(),
        readyAt = estimatedReadyAt,
        note = note,
        photos = masterPhotos,
    )
}

fun OrderStatusDto.toDomain(): OrderStatus = when (this) {
    OrderStatusDto.Accepted -> OrderStatus.Accepted
    OrderStatusDto.Cutting -> OrderStatus.Cutting
    OrderStatusDto.Sewing -> OrderStatus.Sewing
    OrderStatusDto.Ready -> OrderStatus.Ready
    OrderStatusDto.Completed -> OrderStatus.Completed
    OrderStatusDto.Cancelled -> OrderStatus.Cancelled
}

fun OrderStatus.toDto(): OrderStatusDto = when (this) {
    OrderStatus.Accepted -> OrderStatusDto.Accepted
    OrderStatus.Cutting -> OrderStatusDto.Cutting
    OrderStatus.Sewing -> OrderStatusDto.Sewing
    OrderStatus.Ready -> OrderStatusDto.Ready
    OrderStatus.Completed -> OrderStatusDto.Completed
    OrderStatus.Cancelled -> OrderStatusDto.Cancelled
}

/** «Каштан · 38 · Стандарт». */
private fun OrderItemDto.options(): String =
    listOfNotNull(shadeName, size, heightId?.let(::heightName)).filter { it.isNotBlank() }.joinToString(" · ")

private fun OrderDto.customer(user: UserDto?): OrderCustomer {
    val phone = contactPhone.ifBlank { user?.phone.orEmpty() }
    val size = user?.size?.let { size ->
        val label = listOf("размер ${size.label}", heightName(size.heightId).lowercase()).filter { it.isNotBlank() }.joinToString(" ")
        val foot = size.footLengthCm?.let { " · стопа ${it.centimeters()} см" }.orEmpty()
        label + foot
    }

    return OrderCustomer(
        name = user?.name?.takeIf { it.isNotBlank() } ?: "Без имени",
        phone = phone.formatPhone(),
        phoneDial = phone,
        size = size ?: "размер не указан",
    )
}

private fun OrderDeliveryDto.toDomain(): OrderDelivery {
    val place = address?.let { address ->
        listOf(address.city, address.street, address.flat.takeIf { it.isNotBlank() }?.let { "кв. $it" })
            .filter { !it.isNullOrBlank() }
            .joinToString(", ")
    }
    return OrderDelivery(
        short = listOfNotNull(title, address?.city?.takeIf { it.isNotBlank() }).joinToString(", "),
        full = listOfNotNull(title, place?.takeIf { it.isNotBlank() }).joinToString(" · "),
    )
}

private fun heightName(id: String): String = when (id) {
    "standard" -> "Стандарт"
    "tall" -> "Высокие"
    else -> id
}

/** 24.5 → «24,5», 25.0 → «25». */
private fun Double.centimeters(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString().replace('.', ',')

private const val DEFAULT_SHADE = 0xFFB98A5E
