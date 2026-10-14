package com.ugg.admin.kit.catalog.data.useCases

import com.ugg.admin.kit.catalog.domain.entities.Catalog
import com.ugg.admin.kit.catalog.domain.useCases.CatalogUseCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

/** The catalog kept in memory until a restart. Bind one instance for the whole app. */
class MockCatalogUseCase(
    seed: Catalog = Catalog.mock,
    private val latency: Duration = 300.milliseconds,
) : CatalogUseCase {
    private var catalog = seed

    override suspend fun fetch(): Catalog {
        delay(latency)
        return catalog
    }

    override suspend fun save(catalog: Catalog) {
        delay(latency)
        this.catalog = catalog
    }
}
