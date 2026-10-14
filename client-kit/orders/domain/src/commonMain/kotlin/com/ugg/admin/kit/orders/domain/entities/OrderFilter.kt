package com.ugg.admin.kit.orders.domain.entities

/** Chips above the order list. */
enum class OrderFilter {
    New,
    InWork,
    Ready,
    All,
    ;

    fun matches(status: OrderStatus): Boolean = when (this) {
        New -> status == OrderStatus.Accepted
        InWork -> status == OrderStatus.Cutting || status == OrderStatus.Sewing
        Ready -> status == OrderStatus.Ready
        All -> true
    }
}
