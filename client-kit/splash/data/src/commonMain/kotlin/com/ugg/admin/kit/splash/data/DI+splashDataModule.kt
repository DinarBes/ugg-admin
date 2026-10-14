package com.ugg.admin.kit.splash.data

import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.kit.core.domain.AppEnvironment
import com.ugg.admin.kit.splash.data.repositories.MockSplashRepository
import com.ugg.admin.kit.splash.data.repositories.SharedSplashRepository
import com.ugg.admin.kit.splash.domain.useCases.SplashUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on

val splashDataModule get() = DI.Module("splash-data") {
    // PRODUCTION: Firebase phone sign-in plus `isAdmin` in the profile. MOCK: the template's keystore
    // session and code 123456.
    bind<SplashUseCase>() with factory {
        val core: TemplateCore = instance()

        when (instance<AppEnvironment>()) {
            AppEnvironment.PRODUCTION -> SharedSplashRepository(on(core).instance(), on(core).instance())
            AppEnvironment.MOCK -> MockSplashRepository(instance(), instance())
        }
    }
}
