package com.ugg.admin.kit.splash.data.repositories

import com.ugg.admin.infrastructure.api.auth.TemplateAuth
import com.ugg.admin.infrastructure.api.auth.context.AuthEvent
import com.ugg.admin.infrastructure.keystore.session.AccessService
import com.ugg.admin.infrastructure.keystore.session.AuthorizationStatus
import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import com.ugg.admin.kit.splash.domain.exceptions.KeystoreUnavailableException
import com.ugg.admin.kit.splash.domain.useCases.SplashUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class SharedSplashRepository(
    private val auth: TemplateAuth,
    private val access: AccessService,
): SplashUseCase {
    private val context by auth.context

    override suspend fun getInitialFlow(): SplashFlow {
        delay(2.seconds)

        return when (access.authorizationStatus()) {
            AuthorizationStatus.Authorized -> SplashFlow.Launch
            AuthorizationStatus.NotAuthorized -> SplashFlow.Auth
            // Credentials unreadable — most likely a background launch on a locked device. Throw
            // rather than guess: the splash screen stays put and no one gets signed out.
            AuthorizationStatus.Undetermined -> throw KeystoreUnavailableException()
        }
    }

    override fun flows(): Flow<SplashFlow> = context.event.map {
        when (it) {
            AuthEvent.Login -> SplashFlow.Launch
            AuthEvent.Logout -> SplashFlow.Auth
        }
    }
}
