package com.ugg.admin.kit.catalog.presentation.external

import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.kit.catalog.data.useCases.MockCatalogUseCase
import com.ugg.admin.kit.catalog.data.useCases.SharedCatalogUseCase
import com.ugg.admin.kit.catalog.domain.useCases.CatalogUseCase
import com.ugg.admin.kit.catalog.presentation.external.view.models.CatalogViewModel
import com.ugg.admin.kit.core.domain.AppEnvironment
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on

// One catalog for the whole app, outside Kodein: a binding of the Mock type would also match CatalogUseCase.
private val mockCatalog by lazy { MockCatalogUseCase() }

val catalogModule: DI.Module
    get() = DI.Module("catalog.module") {
        bind<CatalogViewModel>() with factory {
            CatalogViewModel(instance())
        }

        bind<CatalogUseCase>() with factory {
            // PRODUCTION edits `products/classic` in Firestore (CatalogService, scoped in core/data).
            val core: TemplateCore = instance()

            when (instance<AppEnvironment>()) {
                AppEnvironment.PRODUCTION -> SharedCatalogUseCase(on(core).instance())
                AppEnvironment.MOCK -> mockCatalog
            }
        }
    }
