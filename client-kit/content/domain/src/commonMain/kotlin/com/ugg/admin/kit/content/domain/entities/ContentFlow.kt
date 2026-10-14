package com.ugg.admin.kit.content.domain.entities

import com.ugg.admin.kit.navigation.domain.external.entities.Destination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("destination")
sealed class ContentFlow : Destination() {
    /** The editor of one section of the client app's texts. Covers the tab bar. */
    @Serializable
    data class Editor(val section: ContentSection) : ContentFlow() {
        override val isTabViewVisible = false
    }
}
