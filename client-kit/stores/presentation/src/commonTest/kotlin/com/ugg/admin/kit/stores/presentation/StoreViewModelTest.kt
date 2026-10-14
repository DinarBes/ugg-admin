@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.stores.presentation

import com.ugg.admin.kit.stores.data.useCases.MockStoresUseCase
import com.ugg.admin.kit.stores.presentation.external.view.models.StoreViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    // Wednesday 14 October 2026, 12:00 in Irkutsk.
    private val clock = object : Clock {
        override fun now() = Instant.parse("2026-10-14T04:00:00Z")
    }

    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun formShowsTheStoreAndPreview() = runTest {
        val vm = StoreViewModel(MockStoresUseCase(latency = Duration.ZERO), clock)
        vm.load("listvyanka")
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals("+7 900 000-00-01", state.phone)
        assertEquals("Пт–Вс 11:00–19:00 · Откроется в пт", state.preview)
        assertFalse(state.hasChanges)
    }

    @Test
    fun openingWednesdayChangesPreviewAndSaves() = runTest {
        val useCase = MockStoresUseCase(latency = Duration.ZERO)
        val vm = StoreViewModel(useCase, clock)
        vm.load("listvyanka")
        advanceUntilIdle()

        vm.onWorkingChange(DayOfWeek.WEDNESDAY, true)
        assertEquals("Ср 11:00–19:00, Пт–Вс 11:00–19:00 · Открыто до 19:00", vm.state.value.preview)

        vm.onSave()
        advanceUntilIdle()

        assertTrue(vm.state.value.isJustSaved)
        val saved = useCase.observeStores().first().first { it.id == "listvyanka" }
        assertTrue(saved.schedule.first { it.day == DayOfWeek.WEDNESDAY }.isWorking)
    }

    @Test
    fun badPhoneBlocksSaving() = runTest {
        val vm = StoreViewModel(MockStoresUseCase(latency = Duration.ZERO), clock)
        vm.load("irkutsk")
        advanceUntilIdle()

        vm.onPhoneChange("+7 900")

        assertNull(vm.state.value.draft)
        assertFalse(vm.state.value.canSave)
    }
}
