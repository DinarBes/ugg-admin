package com.ugg.admin.kit.settings.domain.useCases

import com.ugg.admin.kit.settings.domain.entities.Account
import kotlinx.coroutines.flow.Flow

/**
 * The signed-in account. Signing out is not here: the session belongs to the splash, which also
 * takes the app back to the phone step afterwards.
 */
interface SettingsUseCase {
    /** `null` while nobody is signed in (a moment during sign-out). */
    fun observeAccount(): Flow<Account?>
}
