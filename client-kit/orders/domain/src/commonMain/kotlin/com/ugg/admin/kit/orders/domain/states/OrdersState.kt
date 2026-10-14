package com.ugg.admin.kit.orders.domain.states

import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderFilter
import com.ugg.admin.kit.orders.domain.entities.OrderStatus

data class OrdersState(
    val orders: List<Order> = emptyList(),
    val filter: OrderFilter = OrderFilter.New,
    /** «Сегодня, 14 октября». */
    val today: String = "",
    val isSearching: Boolean = false,
    val query: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null,
) {
    val newCount: Int get() = orders.count { OrderFilter.New.matches(it.status) }
    val inWorkCount: Int get() = orders.count { OrderFilter.InWork.matches(it.status) }
    val readyCount: Int get() = orders.count { it.status == OrderStatus.Ready }

    /**
     * What the list shows. A search looks through every order, whatever the chip — the master
     * searching for «Морозова» wants her old orders too.
     */
    val visibleOrders: List<Order>
        get() {
            val needle = query.trim()
            if (isSearching && needle.isNotEmpty()) {
                val digits = needle.filter { it.isDigit() }
                return orders.filter { order ->
                    order.customer.name.contains(needle, ignoreCase = true) ||
                        (digits.isNotEmpty() && (order.number.toString().contains(digits) || order.customer.phoneDial.contains(digits)))
                }
            }
            return orders.filter { filter.matches(it.status) }
        }
}
