package com.ugg.admin.kit.splash.presentation.external.states

data class SplashState(
    val error: Throwable? = null,
    /** «Проверить ещё раз» or «Выйти» on the no-access screen is running. */
    val isChecking: Boolean = false,
)
