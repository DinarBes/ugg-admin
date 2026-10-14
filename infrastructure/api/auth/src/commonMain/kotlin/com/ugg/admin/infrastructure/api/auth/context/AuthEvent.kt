package com.ugg.admin.infrastructure.api.auth.context

sealed class AuthEvent {
    data object Login : AuthEvent()
    data object Logout : AuthEvent()
}
