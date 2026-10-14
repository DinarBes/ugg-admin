package com.ugg.admin.infrastructure.firebase.firestore

import com.ugg.admin.infrastructure.firebase.firestore.dto.OrderStatus
import com.ugg.admin.infrastructure.firebase.firestore.dto.StoreDto
import com.ugg.admin.infrastructure.firebase.firestore.services.FirestoreCatalogService
import com.ugg.admin.infrastructure.firebase.firestore.services.FirestoreOrderService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class AdminServicesTest {
    @Test
    fun statusIsWrittenAsFirestoreSpellsIt() = runTest {
        val store = FakeDocumentStore(mapOf("orders/o1" to mapOf("number" to 1027L, "status" to "accepted", "userId" to "u1")))
        val service = FirestoreOrderService(store)

        service.setStatus("o1", OrderStatus.Cutting)

        assertEquals("cutting", store.documents.value["orders/o1"]?.get("status"))
        assertEquals(1027L, store.documents.value["orders/o1"]?.get("number"))
        assertEquals(OrderStatus.Cutting, service.observeOrder("o1").first()?.status)
    }

    @Test
    fun noteLivesOutsideTheOrder() = runTest {
        val store = FakeDocumentStore(mapOf("orders/o1" to mapOf("number" to 1027L)))
        val service = FirestoreOrderService(store)

        service.saveNote("o1", "мех погуще")

        assertEquals("мех погуще", service.observeNote("o1").first())
        assertNull(store.documents.value["orders/o1"]?.get("text"))
    }

    @Test
    fun dayOffIsWrittenAsNull() = runTest {
        val store = FakeDocumentStore(
            mapOf("stores/s1" to mapOf("schedule" to mapOf("mon" to mapOf("open" to "10:00", "close" to "20:00")))),
        )
        val service = FirestoreCatalogService(store)

        service.saveStore(StoreDto(id = "s1", city = "Листвянка", schedule = StoreDto.ScheduleDto(fri = StoreDto.HoursDto("11:00", "19:00"))))

        @Suppress("UNCHECKED_CAST")
        val schedule = store.documents.value["stores/s1"]?.get("schedule") as Map<String, Any?>
        assertTrue("mon" in schedule)
        assertNull(schedule["mon"])
        assertEquals(mapOf("open" to "11:00", "close" to "19:00"), schedule["fri"])
    }
}
