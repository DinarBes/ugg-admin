package com.ugg.admin.kit.orders.domain.entities

import com.ugg.admin.kit.navigation.domain.external.entities.Destination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("destination")
sealed class OrdersFlow : Destination() {
    /** One order: stage, customer, items, deadline, master's note. Covers the tab bar. */
    @Serializable
    data class Detail(val orderId: String) : OrdersFlow() {
        override val isTabViewVisible = false
    }
}
