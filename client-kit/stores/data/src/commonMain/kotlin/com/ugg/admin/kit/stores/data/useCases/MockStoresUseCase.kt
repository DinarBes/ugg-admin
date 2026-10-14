package com.ugg.admin.kit.stores.data.useCases

import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.exceptions.StoreNotFoundException
import com.ugg.admin.kit.stores.domain.useCases.StoresUseCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Stores kept in memory until a restart. Bind one instance for the whole app. */
class MockStoresUseCase(
    seed: List<Store> = Store.mocks,
    private val latency: Duration = 300.milliseconds,
) : StoresUseCase {
    private val stores = MutableStateFlow(seed)

    override fun observeStores(): Flow<List<Store>> = stores.map { list -> list.sortedBy { it.order } }

    override suspend fun store(storeId: String): Store {
        delay(latency)
        return stores.value.firstOrNull { it.id == storeId } ?: throw StoreNotFoundException()
    }

    override suspend fun save(store: Store) {
        delay(latency)
        if (stores.value.none { it.id == store.id }) throw StoreNotFoundException()

        stores.update { list -> list.map { if (it.id == store.id) store else it } }
    }
}
