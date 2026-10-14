package com.ugg.admin.kit.auth.data.repositories

import com.ugg.admin.infrastructure.api.auth.services.AuthService
import com.ugg.admin.kit.auth.domain.useCases.AuthUseCase

class SharedAuthRepository(
    val authService: AuthService
) : AuthUseCase {
    override suspend fun initiate(phone: String): String = authService.initiate(phone)

    override suspend fun login(otp: String, sessionId: String) = authService.login(otp, sessionId)
}
