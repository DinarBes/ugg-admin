package com.ugg.admin.infrastructure.firebase.firestore.services

import com.ugg.admin.infrastructure.firebase.firestore.FirestoreException
import com.ugg.admin.infrastructure.firebase.firestore.dto.ProductDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.StoreDto
import com.ugg.admin.infrastructure.firebase.firestore.internal.DocumentStore
import com.ugg.admin.infrastructure.firebase.firestore.internal.FirestoreJson
import com.ugg.admin.infrastructure.firebase.firestore.internal.RawQuery
import com.ugg.admin.infrastructure.firebase.firestore.internal.ServerTimestamp
import com.ugg.admin.infrastructure.firebase.firestore.internal.decode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The product and the stores, read like the client app reads them and written by the workshop.
 * Writes need `isAdmin: true` in the signed-in user's profile.
 */
interface CatalogService {
    /** @throws FirestoreException `NotFound` when the product isn't seeded. */
    suspend fun product(id: String = DEFAULT_PRODUCT_ID): ProductDto

    /**
     * Writes only what the «Каталог» tab edits — price, lead time, shades, sizes — so texts,
     * photos and features edited elsewhere stay as they are.
     */
    suspend fun saveProductOptions(product: ProductDto)

    /** Sorted by their `order` field; live. */
    fun observeStores(): Flow<List<StoreDto>>

    /** Contacts, location and the whole week; a day off is written as `null`. */
    suspend fun saveStore(store: StoreDto)

    companion object {
        const val DEFAULT_PRODUCT_ID = "classic"
    }
}

internal class FirestoreCatalogService(
    private val store: DocumentStore,
) : CatalogService {

    override suspend fun product(id: String): ProductDto =
        (store.get("$PRODUCTS/$id") ?: throw FirestoreException(FirestoreException.Reason.NotFound))
            .decode(ProductDto.serializer())

    override suspend fun saveProductOptions(product: ProductDto) {
        val data = FirestoreJson.encode(ProductDto.serializer(), product).filterKeys { it in PRODUCT_OPTIONS }
        store.set("$PRODUCTS/${product.id}", data + ("updatedAt" to ServerTimestamp), merge = true)
    }

    override fun observeStores(): Flow<List<StoreDto>> =
        store.observe(RawQuery(collection = STORES, orderBy = "order"))
            .map { documents -> documents.map { it.decode(StoreDto.serializer()) } }

    override suspend fun saveStore(store: StoreDto) {
        // Nulls are dropped when encoding, and a merge would then keep the old hours of a day
        // that became a day off — so the week is spelled out with explicit nulls.
        val schedule = with(store.schedule) {
            mapOf("mon" to mon, "tue" to tue, "wed" to wed, "thu" to thu, "fri" to fri, "sat" to sat, "sun" to sun)
                .mapValues { (_, hours) -> hours?.let { mapOf("open" to it.open, "close" to it.close) } }
        }
        val data = FirestoreJson.encode(StoreDto.serializer(), store) + mapOf("schedule" to schedule, "updatedAt" to ServerTimestamp)
        this.store.set("$STORES/${store.id}", data, merge = true)
    }

    private companion object {
        const val PRODUCTS = "products"
        const val STORES = "stores"
        val PRODUCT_OPTIONS = setOf("price", "leadTimeDays", "shades", "sizes")
    }
}
