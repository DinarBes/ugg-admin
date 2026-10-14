package com.template.client.kit.auth.data

import com.template.client.infrastructure.api.auth.services.AuthService
import com.template.client.infrastructure.api.core.TemplateCore
import com.template.client.kit.auth.data.repositories.MockAuthRepository
import com.template.client.kit.auth.data.repositories.SharedAuthRepository
import com.template.client.kit.auth.domain.useCases.AuthUseCase
import com.template.client.kit.core.data.scopes.TemplateCoreScope
import com.template.client.kit.core.domain.AppEnvironment
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on
import org.kodein.di.scoped
import org.kodein.di.singleton

val authDataModule get() = DI.Module("auth-data") {
    // TemplateAuth is bound at the composition root (TemplateApp), which owns the keystore and
    // supplies the token-loading block. The feature only consumes it.
    bind<AuthUseCase>() with factory {
        val core: TemplateCore = instance()

        when (instance<AppEnvironment>()) {
            AppEnvironment.PRODUCTION -> SharedAuthRepository(on(core).instance())
            AppEnvironment.MOCK -> MockAuthRepository(instance())
        }
    }
}