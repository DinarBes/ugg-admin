package com.ugg.admin.kit.settings.domain.entities

import com.ugg.admin.kit.navigation.domain.external.entities.Destination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("destination")
sealed class SettingsFlow : Destination() {
    /** The account and «Выйти». Opened from the orders screen, covers the tab bar. */
    @Serializable
    data object Settings : SettingsFlow() {
        override val isTabViewVisible = false
    }
}
