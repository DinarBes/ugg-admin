package com.template.client.kit.auth.presentation.external.states

data class OtpState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
