package com.ugg.admin.kit.clients.presentation.external

import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.kit.clients.data.useCases.MockClientsUseCase
import com.ugg.admin.kit.clients.data.useCases.SharedClientsUseCase
import com.ugg.admin.kit.clients.domain.useCases.ClientsUseCase
import com.ugg.admin.kit.clients.presentation.external.view.models.ClientsViewModel
import com.ugg.admin.kit.core.domain.AppEnvironment
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on

private val mockClients by lazy { MockClientsUseCase() }

val clientsModule: DI.Module
    get() = DI.Module("clients.module") {
        bind<ClientsViewModel>() with factory {
            ClientsViewModel(instance())
        }

        bind<ClientsUseCase>() with factory {
            // PRODUCTION reads `users` and `orders` in Firestore (services scoped in core/data).
            val core: TemplateCore = instance()

            when (instance<AppEnvironment>()) {
                AppEnvironment.PRODUCTION -> SharedClientsUseCase(on(core).instance(), on(core).instance())
                AppEnvironment.MOCK -> mockClients
            }
        }
    }
