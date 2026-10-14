package com.ugg.admin.kit.stores.presentation.external

import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.kit.core.domain.AppEnvironment
import com.ugg.admin.kit.stores.data.useCases.MockStoresUseCase
import com.ugg.admin.kit.stores.data.useCases.SharedStoresUseCase
import com.ugg.admin.kit.stores.domain.useCases.StoresUseCase
import com.ugg.admin.kit.stores.presentation.external.view.models.StoreViewModel
import com.ugg.admin.kit.stores.presentation.external.view.models.StoresViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on

// One store list for the whole app, outside Kodein: a binding of the Mock type would also match StoresUseCase.
private val mockStores by lazy { MockStoresUseCase() }

val storesModule: DI.Module
    get() = DI.Module("stores.module") {
        bind<StoresViewModel>() with factory {
            StoresViewModel(instance())
        }

        bind<StoreViewModel>() with factory {
            StoreViewModel(instance())
        }

        bind<StoresUseCase>() with factory {
            // PRODUCTION edits `stores` in Firestore (CatalogService, scoped in core/data).
            val core: TemplateCore = instance()

            when (instance<AppEnvironment>()) {
                AppEnvironment.PRODUCTION -> SharedStoresUseCase(on(core).instance())
                AppEnvironment.MOCK -> mockStores
            }
        }
    }
