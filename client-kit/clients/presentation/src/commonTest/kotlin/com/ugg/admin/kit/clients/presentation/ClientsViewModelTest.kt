package com.ugg.admin.kit.clients.presentation

import com.ugg.admin.kit.clients.data.useCases.MockClientsUseCase
import com.ugg.admin.kit.clients.presentation.external.view.models.ClientsViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ClientsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun viewModel() = ClientsViewModel(MockClientsUseCase(latency = Duration.ZERO))

    @Test
    fun activeOrdersComeFirstNewestOnTop() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        val clients = vm.state.value.visibleClients
        assertEquals(1027L, clients.first().activeOrder?.number)
        assertEquals(null, clients.last().activeOrder)
        assertEquals(10, vm.state.value.activeCount)
    }

    @Test
    fun searchByNameOrPhoneDigits() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onQuery("морозова")
        assertEquals(listOf("u4"), vm.state.value.visibleClients.map { it.id })

        vm.onQuery("950 222")
        assertEquals(listOf("u13"), vm.state.value.visibleClients.map { it.id })
    }
}
