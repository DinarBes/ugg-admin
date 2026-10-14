package com.ugg.admin.kit.splash.domain.useCases

import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import kotlinx.coroutines.flow.Flow

interface SplashUseCase {
    /** @throws com.ugg.admin.kit.splash.domain.exceptions.KeystoreUnavailableException */
    suspend fun getInitialFlow(): SplashFlow

    fun flows(): Flow<SplashFlow>
}
