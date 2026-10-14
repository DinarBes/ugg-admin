package com.ugg.admin.kit.stores.domain.useCases

import com.ugg.admin.kit.stores.domain.entities.Store
import kotlinx.coroutines.flow.Flow

interface StoresUseCase {
    /** All stores in display order; updates after [save]. */
    fun observeStores(): Flow<List<Store>>

    /** Throws [com.ugg.admin.kit.stores.domain.exceptions.StoreNotFoundException]. */
    suspend fun store(storeId: String): Store

    /** Customers see the new contacts and hours on their next visit to «Точки». */
    suspend fun save(store: Store)
}
