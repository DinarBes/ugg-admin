package com.ugg.admin.kit.settings.domain.states

import com.ugg.admin.kit.settings.domain.entities.Account

data class SettingsState(
    val account: Account? = null,
    val isSigningOut: Boolean = false,
    val error: Throwable? = null,
)
