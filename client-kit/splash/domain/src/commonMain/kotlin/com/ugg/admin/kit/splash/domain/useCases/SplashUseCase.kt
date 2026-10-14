package com.ugg.admin.kit.splash.domain.useCases

import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import kotlinx.coroutines.flow.Flow

/** Who is signed in and whether they may use the admin app. */
interface SplashUseCase {
    /** @throws com.ugg.admin.kit.splash.domain.exceptions.KeystoreUnavailableException */
    suspend fun getInitialFlow(): SplashFlow

    /** Where to go after every sign-in and sign-out. */
    fun flows(): Flow<SplashFlow>

    /** «Проверить ещё раз» on [SplashFlow.NoAccess] — e.g. right after access was granted. */
    suspend fun recheck(): SplashFlow

    /** [flows] then reports [SplashFlow.Auth]. */
    suspend fun signOut()
}
