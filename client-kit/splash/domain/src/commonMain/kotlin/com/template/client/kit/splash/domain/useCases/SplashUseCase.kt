package com.template.client.kit.splash.domain.useCases

import com.template.client.kit.splash.domain.entities.SplashFlow
import kotlinx.coroutines.flow.Flow

interface SplashUseCase {
    /** @throws com.template.client.kit.splash.domain.exceptions.KeystoreUnavailableException */
    suspend fun getInitialFlow(): SplashFlow

    fun flows(): Flow<SplashFlow>
}
