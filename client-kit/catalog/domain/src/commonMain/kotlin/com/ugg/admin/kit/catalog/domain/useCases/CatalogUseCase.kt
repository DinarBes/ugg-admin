package com.ugg.admin.kit.catalog.domain.useCases

import com.ugg.admin.kit.catalog.domain.entities.Catalog

interface CatalogUseCase {
    suspend fun fetch(): Catalog

    /** Writes price, lead time, shades and sizes; the client app picks them up on its next read. */
    suspend fun save(catalog: Catalog)
}
