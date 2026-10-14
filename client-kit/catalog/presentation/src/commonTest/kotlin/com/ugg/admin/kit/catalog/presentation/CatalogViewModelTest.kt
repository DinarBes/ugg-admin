package com.ugg.admin.kit.catalog.presentation

import com.ugg.admin.kit.catalog.data.useCases.MockCatalogUseCase
import com.ugg.admin.kit.catalog.presentation.external.view.models.CatalogViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun loadsTheFormFromTheCatalog() = runTest {
        val vm = CatalogViewModel(MockCatalogUseCase(latency = Duration.ZERO))
        advanceUntilIdle()

        assertEquals("14900", vm.state.value.price)
        assertEquals("5–7", vm.state.value.leadTime)
        assertFalse(vm.state.value.hasChanges)
    }

    @Test
    fun savedEditsSurviveAReload() = runTest {
        val useCase = MockCatalogUseCase(latency = Duration.ZERO)
        val vm = CatalogViewModel(useCase)
        advanceUntilIdle()

        vm.onSizeToggle("44")
        vm.onShadeStockChange("milk", true)
        vm.onAddShade("Мокко", 0xFF8B6A50)
        assertTrue(vm.state.value.canSave)

        vm.onSave()
        advanceUntilIdle()
        assertTrue(vm.state.value.isJustSaved)
        assertFalse(vm.state.value.hasChanges)

        val saved = useCase.fetch()
        assertTrue(saved.sizes.first { it.label == "44" }.isAvailable)
        assertTrue(saved.shades.first { it.id == "milk" }.inStock)
        assertEquals("mokko", saved.shades.last().id)
    }

    @Test
    fun refreshKeepsUnsavedEdits() = runTest {
        val vm = CatalogViewModel(MockCatalogUseCase(latency = Duration.ZERO))
        advanceUntilIdle()

        vm.onPriceChange("15900")
        vm.refresh()
        advanceUntilIdle()

        assertEquals("15900", vm.state.value.price)
        assertTrue(vm.state.value.hasChanges)
        assertFalse(vm.state.value.isRefreshing)
    }
}
