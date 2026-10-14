package com.template.client.kit.auth.domain.useCases

interface AuthUseCase {
    suspend fun initiate(phone: String): String
    suspend fun login(otp: String, sessionId: String)
}
