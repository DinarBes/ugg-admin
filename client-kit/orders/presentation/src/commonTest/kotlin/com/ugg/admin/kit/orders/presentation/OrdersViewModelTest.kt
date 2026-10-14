package com.ugg.admin.kit.orders.presentation

import com.ugg.admin.kit.orders.data.useCases.MockOrdersUseCase
import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderCustomer
import com.ugg.admin.kit.orders.domain.entities.OrderFilter
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.orders.presentation.external.view.models.OrderDetailViewModel
import com.ugg.admin.kit.orders.presentation.external.view.models.OrdersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val seed = listOf(
        Order(id = "a", number = 1027, status = OrderStatus.Accepted, customer = OrderCustomer("Анна Королёва", phoneDial = "+79001234567")),
        Order(id = "b", number = 1024, status = OrderStatus.Sewing, customer = OrderCustomer("Ирина Морозова", phoneDial = "+79027650011")),
        Order(id = "c", number = 1017, status = OrderStatus.Completed, customer = OrderCustomer("Ирина Морозова", phoneDial = "+79027650011")),
    )

    private fun useCase() = MockOrdersUseCase(seed, latency = Duration.ZERO)

    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun countsAndDefaultFilter() = runTest {
        val vm = OrdersViewModel(useCase())
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(1, state.newCount)
        assertEquals(1, state.inWorkCount)
        assertEquals(listOf("a"), state.visibleOrders.map { it.id })
        assertFalse(state.isLoading)
    }

    @Test
    fun searchLooksThroughEveryOrder() = runTest {
        val vm = OrdersViewModel(useCase())
        advanceUntilIdle()

        vm.onToggleSearch()
        vm.onQuery("морозова")

        assertEquals(listOf("b", "c"), vm.state.value.visibleOrders.map { it.id })
    }

    @Test
    fun filterClosesSearch() = runTest {
        val vm = OrdersViewModel(useCase())
        advanceUntilIdle()

        vm.onToggleSearch()
        vm.onFilter(OrderFilter.All)

        assertFalse(vm.state.value.isSearching)
        assertEquals(3, vm.state.value.visibleOrders.size)
    }

    @Test
    fun advanceShowsUpInTheList() = runTest {
        val useCase = useCase()
        val list = OrdersViewModel(useCase)
        val detail = OrderDetailViewModel(useCase)

        detail.load("a")
        advanceUntilIdle()
        detail.onAdvance()
        advanceUntilIdle()

        assertEquals(OrderStatus.Cutting, detail.state.value.order?.status)
        assertEquals(0, list.state.value.newCount)
        assertEquals(2, list.state.value.inWorkCount)
    }

    @Test
    fun savingTheNoteClearsChanges() = runTest {
        val detail = OrderDetailViewModel(useCase())

        detail.load("b")
        advanceUntilIdle()
        detail.onNoteChange("мех погуще")
        assertTrue(detail.state.value.hasChanges)

        detail.onSave()
        advanceUntilIdle()

        assertEquals("мех погуще", detail.state.value.order?.note)
        assertFalse(detail.state.value.hasChanges)
    }
}
