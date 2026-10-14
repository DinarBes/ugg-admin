package com.ugg.admin.kit.settings.data.useCases

import com.ugg.admin.kit.settings.domain.entities.Account
import com.ugg.admin.kit.settings.domain.useCases.SettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockSettingsUseCase : SettingsUseCase {
    override fun observeAccount(): Flow<Account?> = flowOf(Account.mock)
}
