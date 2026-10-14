package com.ugg.admin.kit.settings.data.useCases

import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import com.ugg.admin.infrastructure.firebase.firestore.services.UserService
import com.ugg.admin.kit.core.data.content.translatingContentErrors
import com.ugg.admin.kit.core.data.formatting.formatPhone
import com.ugg.admin.kit.settings.domain.entities.Account
import com.ugg.admin.kit.settings.domain.useCases.SettingsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** The Firebase user plus their `users/{uid}` profile for the name. */
class SharedSettingsUseCase(
    private val phoneAuth: PhoneAuthService,
    private val users: UserService,
) : SettingsUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAccount(): Flow<Account?> = phoneAuth.observeUser().flatMapLatest { user ->
        if (user == null) {
            flowOf(null)
        } else {
            users.observeUser(user.uid).map { profile ->
                Account(
                    name = profile?.name?.takeIf { it.isNotBlank() },
                    phone = (user.phone ?: profile?.phone).orEmpty().formatPhone(),
                )
            }
        }
    }.translatingContentErrors()
}
