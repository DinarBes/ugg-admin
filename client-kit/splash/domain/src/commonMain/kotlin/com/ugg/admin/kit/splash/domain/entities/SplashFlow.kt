package com.ugg.admin.kit.splash.domain.entities

import com.ugg.admin.kit.navigation.domain.external.entities.Destination
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("destination")
sealed class SplashFlow : Destination() {
    @Serializable
    data object Auth : SplashFlow() {
        override val isTabViewVisible = false
    }

    @Serializable
    data object Launch : SplashFlow() {
        override val isTabViewVisible = false
    }

    /**
     * Signed in, but the number has no admin access ([isOffline] `false`), or it couldn't be
     * checked for lack of network ([isOffline] `true`).
     */
    @Serializable
    data class NoAccess(val phone: String, val isOffline: Boolean) : SplashFlow() {
        override val isTabViewVisible = false
    }
}
