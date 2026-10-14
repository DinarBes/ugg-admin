package com.ugg.admin.kit.settings.presentation.external

import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.kit.core.domain.AppEnvironment
import com.ugg.admin.kit.settings.data.useCases.MockSettingsUseCase
import com.ugg.admin.kit.settings.data.useCases.SharedSettingsUseCase
import com.ugg.admin.kit.settings.domain.useCases.SettingsUseCase
import com.ugg.admin.kit.settings.presentation.external.view.models.SettingsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on

val settingsModule: DI.Module
    get() = DI.Module("settings.module") {
        bind<SettingsViewModel>() with factory {
            SettingsViewModel(instance())
        }

        bind<SettingsUseCase>() with factory {
            // PRODUCTION: Firebase Auth plus `users/{uid}` (services scoped in core/data).
            val core: TemplateCore = instance()

            when (instance<AppEnvironment>()) {
                AppEnvironment.PRODUCTION -> SharedSettingsUseCase(on(core).instance(), on(core).instance())
                AppEnvironment.MOCK -> MockSettingsUseCase()
            }
        }
    }
