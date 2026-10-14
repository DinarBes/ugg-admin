package com.ugg.admin.kit.stores.data.useCases

import com.ugg.admin.infrastructure.firebase.firestore.services.CatalogService
import com.ugg.admin.kit.core.data.content.translatingContentErrors
import com.ugg.admin.kit.stores.data.mappers.applyTo
import com.ugg.admin.kit.stores.data.mappers.toDomain
import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.exceptions.StoreNotFoundException
import com.ugg.admin.kit.stores.domain.useCases.StoresUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** `stores` in Firestore — what the client app's «Точки» tab shows. */
class SharedStoresUseCase(
    private val catalog: CatalogService,
) : StoresUseCase {

    override fun observeStores(): Flow<List<Store>> =
        catalog.observeStores().map { stores -> stores.map { it.toDomain() } }.translatingContentErrors()

    override suspend fun store(storeId: String): Store = translatingContentErrors {
        catalog.observeStores().first().firstOrNull { it.id == storeId }?.toDomain()
    } ?: throw StoreNotFoundException()

    override suspend fun save(store: Store) = translatingContentErrors {
        val dto = catalog.observeStores().first().firstOrNull { it.id == store.id } ?: throw StoreNotFoundException()
        catalog.saveStore(store.applyTo(dto))
    }
}
