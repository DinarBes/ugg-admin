package com.template.client.kit.splash.data

import com.template.client.kit.splash.data.repositories.SharedSplashRepository
import com.template.client.kit.splash.domain.useCases.SplashUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

val splashDataModule get() = DI.Module("splash-data") {
    // No AppEnvironment branch: splash reads the keystore in both environments — MOCK swaps the
    // keystore itself (InMemoryKeystore), not the flow that reads it.
    bind<SplashUseCase>() with factory { SharedSplashRepository(instance(), instance()) }
}
