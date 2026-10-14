package com.ugg.admin.kit.catalog.data.useCases

import com.ugg.admin.infrastructure.firebase.firestore.services.CatalogService
import com.ugg.admin.kit.catalog.data.mappers.applyTo
import com.ugg.admin.kit.catalog.data.mappers.toDomain
import com.ugg.admin.kit.catalog.domain.entities.Catalog
import com.ugg.admin.kit.catalog.domain.useCases.CatalogUseCase
import com.ugg.admin.kit.core.data.content.translatingContentErrors

/** `products/classic` in Firestore — the same document the client app's «Модель» tab reads. */
class SharedCatalogUseCase(
    private val catalog: CatalogService,
) : CatalogUseCase {

    override suspend fun fetch(): Catalog = translatingContentErrors { catalog.product().toDomain() }

    override suspend fun save(catalog: Catalog) = translatingContentErrors {
        val product = this.catalog.product(catalog.productId)
        this.catalog.saveProductOptions(catalog.applyTo(product))
    }
}
