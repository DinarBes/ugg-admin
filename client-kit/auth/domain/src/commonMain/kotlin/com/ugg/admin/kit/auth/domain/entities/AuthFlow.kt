package com.ugg.admin.kit.auth.domain.entities

import com.ugg.admin.kit.navigation.domain.external.entities.Destination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("destination")
sealed class AuthFlow : Destination() {
    @Serializable
    data class Otp(
        var phone: String,
        var sessionId: String,
    ) : AuthFlow() {
        override val isTabViewVisible = false
    }
}
