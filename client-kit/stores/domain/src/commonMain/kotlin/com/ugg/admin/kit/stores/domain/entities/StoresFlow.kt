package com.ugg.admin.kit.stores.domain.entities

import com.ugg.admin.kit.navigation.domain.external.entities.Destination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("destination")
sealed class StoresFlow : Destination() {
    /** «Точки»: Иркутск and Листвянка, opened from «Контент». */
    @Serializable
    data object Stores : StoresFlow() {
        override val isTabViewVisible = false
    }

    /** One store: contacts, map point, weekly schedule. */
    @Serializable
    data class Store(val storeId: String) : StoresFlow() {
        override val isTabViewVisible = false
    }
}
