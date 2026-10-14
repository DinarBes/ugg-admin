package com.ugg.admin.kit.splash.data.repositories

import com.ugg.admin.infrastructure.firebase.auth.AuthUser
import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import com.ugg.admin.infrastructure.firebase.auth.PhoneVerification
import com.ugg.admin.infrastructure.firebase.firestore.FirestoreException
import com.ugg.admin.infrastructure.firebase.firestore.dto.UserDto
import com.ugg.admin.infrastructure.firebase.firestore.services.UserService
import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedSplashRepositoryTest {
    private class FakePhoneAuth(user: AuthUser?) : PhoneAuthService {
        private val users = MutableStateFlow(user)
        override fun observeUser(): Flow<AuthUser?> = users
        override val currentUser: AuthUser? get() = users.value
        override suspend fun sendCode(phone: String): PhoneVerification = error("unused")
        override suspend fun verify(verificationId: String, code: String): AuthUser = error("unused")
        override suspend fun signOut() { users.value = null }
        override suspend fun deleteAccount() = Unit
    }

    private class FakeUsers(private val user: Result<UserDto?>) : UserService {
        override fun observeUsers(): Flow<List<UserDto>> = flowOf(emptyList())
        override fun observeUser(uid: String): Flow<UserDto?> = flowOf(user.getOrNull())
        override suspend fun getUser(uid: String): UserDto? = user.getOrThrow()
    }

    private val user = AuthUser(uid = "u1", phone = "+79001234567")

    private fun repository(profile: Result<UserDto?>, signedIn: AuthUser? = user) =
        SharedSplashRepository(FakePhoneAuth(signedIn), FakeUsers(profile))

    @Test
    fun guestGoesToSignIn() = runTest {
        assertEquals(SplashFlow.Auth, repository(Result.success(null), signedIn = null).getInitialFlow())
    }

    @Test
    fun adminLaunchesTheApp() = runTest {
        assertEquals(SplashFlow.Launch, repository(Result.success(UserDto(id = "u1", isAdmin = true))).getInitialFlow())
    }

    @Test
    fun customerIsStopped() = runTest {
        assertEquals(SplashFlow.NoAccess("+79001234567", isOffline = false), repository(Result.success(UserDto(id = "u1"))).getInitialFlow())
    }

    @Test
    fun noProfileIsNoAccess() = runTest {
        assertEquals(SplashFlow.NoAccess("+79001234567", isOffline = false), repository(Result.success(null)).recheck())
    }

    @Test
    fun offlineCheckIsNotADenial() = runTest {
        val offline = Result.failure<UserDto?>(FirestoreException(FirestoreException.Reason.Unavailable))
        assertEquals(SplashFlow.NoAccess("+79001234567", isOffline = true), repository(offline).recheck())
    }
}
