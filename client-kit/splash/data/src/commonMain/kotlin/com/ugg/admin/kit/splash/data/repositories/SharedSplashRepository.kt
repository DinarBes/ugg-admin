package com.ugg.admin.kit.splash.data.repositories

import com.ugg.admin.infrastructure.firebase.auth.AuthUser
import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import com.ugg.admin.infrastructure.firebase.firestore.FirestoreException
import com.ugg.admin.infrastructure.firebase.firestore.services.UserService
import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import com.ugg.admin.kit.splash.domain.useCases.SplashUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

/**
 * Firebase sign-in, the same accounts as the client app. Signing in is not enough: the profile
 * `users/{uid}` must have `isAdmin: true` (set in the console or with `npm run set-admin` in
 * ugg-client/tools/firestore-seed). The security rules check the same field, so this screen is
 * a courtesy — without the flag every admin read and write is refused anyway.
 */
class SharedSplashRepository(
    private val phoneAuth: PhoneAuthService,
    private val users: UserService,
) : SplashUseCase {

    override suspend fun getInitialFlow(): SplashFlow {
        delay(1.seconds)
        return flowFor(phoneAuth.currentUser)
    }

    // The first value is the state at launch, already handled by getInitialFlow.
    override fun flows(): Flow<SplashFlow> = phoneAuth.observeUser()
        .distinctUntilChangedBy { it?.uid }
        .drop(1)
        .map(::flowFor)

    override suspend fun recheck(): SplashFlow = flowFor(phoneAuth.currentUser)

    override suspend fun signOut() = phoneAuth.signOut()

    private suspend fun flowFor(user: AuthUser?): SplashFlow {
        if (user == null) return SplashFlow.Auth

        val phone = user.phone.orEmpty()
        return try {
            // No profile yet (signed in here first, never in the client app) — not an admin.
            if (users.getUser(user.uid)?.isAdmin == true) SplashFlow.Launch else SplashFlow.NoAccess(phone, isOffline = false)
        } catch (e: FirestoreException) {
            SplashFlow.NoAccess(phone, isOffline = e.reason == FirestoreException.Reason.Unavailable)
        }
    }
}
