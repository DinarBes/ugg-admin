package com.template.client.kit.auth.presentation.external.states

data class InitiateState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
