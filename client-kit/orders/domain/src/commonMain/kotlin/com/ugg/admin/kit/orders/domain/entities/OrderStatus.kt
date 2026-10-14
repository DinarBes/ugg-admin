package com.ugg.admin.kit.orders.domain.entities

/** The same stages the client sees in «Мой заказ», spelled as in Firestore `orders/{id}.status`. */
enum class OrderStatus {
    Accepted,
    Cutting,
    Sewing,
    Ready,
    Completed,
    Cancelled,
    ;

    /** Where «следующий этап» leads; `null` once the order is closed. */
    val next: OrderStatus?
        get() = when (this) {
            Accepted -> Cutting
            Cutting -> Sewing
            Sewing -> Ready
            Ready -> Completed
            Completed, Cancelled -> null
        }

    val isActive: Boolean
        get() = this != Completed && this != Cancelled

    companion object {
        /** The stepper on the order card: «Принят → Раскрой → Пошив → Готов». */
        val stages = listOf(Accepted, Cutting, Sewing, Ready)
    }
}
