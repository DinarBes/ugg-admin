package com.ugg.admin.kit.orders.data

import com.ugg.admin.kit.orders.data.useCases.MockOrdersUseCase
import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.orders.domain.exceptions.OrderNotFoundException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration

class MockOrdersUseCaseTest {
    private val seed = listOf(
        Order(id = "a", number = 1, status = OrderStatus.Accepted),
        Order(id = "b", number = 2, status = OrderStatus.Ready),
    )

    private fun useCase() = MockOrdersUseCase(seed, latency = Duration.ZERO)

    @Test
    fun listIsNewestFirst() = runTest {
        assertEquals(listOf(2L, 1L), useCase().observeOrders().first().map { it.number })
    }

    @Test
    fun advanceMovesToTheNextStage() = runTest {
        val useCase = useCase()

        useCase.advance("a")

        assertEquals(OrderStatus.Cutting, useCase.observeOrder("a").first().status)
    }

    @Test
    fun saveDetailsTrimsTheNote() = runTest {
        val useCase = useCase()

        useCase.saveDetails("a", readyAt = 42L, note = "  мех погуще  ")

        val order = useCase.observeOrder("a").first()
        assertEquals(42L, order.readyAt)
        assertEquals("мех погуще", order.note)
    }

    @Test
    fun cancelLeavesClosedOrdersAlone() = runTest {
        val useCase = useCase()

        useCase.advance("b") // ready → completed
        useCase.cancel("b")

        assertEquals(OrderStatus.Completed, useCase.observeOrder("b").first().status)
    }

    @Test
    fun unknownOrderFails() = runTest {
        assertFailsWith<OrderNotFoundException> { useCase().observeOrder("x").first() }
        assertFailsWith<OrderNotFoundException> { useCase().advance("x") }
    }
}
