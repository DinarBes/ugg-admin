package com.template.client.kit.splash.data.repositories

import com.template.client.infrastructure.api.auth.TemplateAuth
import com.template.client.infrastructure.api.auth.context.AuthEvent
import com.template.client.infrastructure.keystore.session.AccessService
import com.template.client.infrastructure.keystore.session.AuthorizationStatus
import com.template.client.kit.splash.domain.entities.SplashFlow
import com.template.client.kit.splash.domain.exceptions.KeystoreUnavailableException
import com.template.client.kit.splash.domain.useCases.SplashUseCase
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
