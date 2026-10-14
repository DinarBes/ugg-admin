@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.stores.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.core.data.formatting.formatPhone
import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.entities.openingLabel
import com.ugg.admin.kit.stores.domain.entities.summary
import com.ugg.admin.kit.stores.domain.states.StoreState
import com.ugg.admin.kit.stores.domain.useCases.StoresUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

/**
 * The store form. ViewModels are shared per type, so the screen calls [load] with its store on
 * every visit.
 */
class StoreViewModel(
    private val useCase: StoresUseCase,
    private val clock: Clock = Clock.System,
) : ViewModel() {
    val state: StateFlow<StoreState>
        field = MutableStateFlow(StoreState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@StoreViewModel::class.toString() }

        state.update { it.copy(error = throwable, isLoading = false, isSaving = false) }
    }

    private var loading: Job? = null
    private var storeId: String? = null

    fun load(storeId: String) {
        this.storeId = storeId
        loading?.cancel()
        state.update { StoreState(isLoading = true) }

        loading = viewModelScope.launch(unexpectedExceptionHandler) {
            val store = useCase.store(storeId)
            state.update { form(store) }
        }
    }

    fun retry() {
        storeId?.let(::load)
    }

    fun onNameChange(name: String) = edit { it.copy(name = name.take(60)) }

    fun onAddressChange(address: String) = edit { it.copy(address = address.take(120)) }

    fun onPhoneChange(phone: String) = edit {
        it.copy(phone = phone.filter { c -> c.isDigit() || c in "+ ()-" }.take(20))
    }

    fun onLatitudeChange(latitude: String) = edit { it.copy(latitude = latitude.take(12)) }

    fun onLongitudeChange(longitude: String) = edit { it.copy(longitude = longitude.take(12)) }

    fun onWorkingChange(day: DayOfWeek, isWorking: Boolean) = edit { state ->
        state.copy(schedule = state.schedule.map { if (it.day == day) it.copy(isWorking = isWorking) else it })
    }

    /** Times come from the hours dialog as «11:00»; it already checked that [close] is after [open]. */
    fun onHoursChange(day: DayOfWeek, open: String, close: String) = edit { state ->
        state.copy(schedule = state.schedule.map { if (it.day == day) it.copy(open = open, close = close) else it })
    }

    fun onSave() {
        val draft = state.value.draft ?: return
        if (!state.value.canSave) return

        viewModelScope.launch(unexpectedExceptionHandler) {
            state.update { it.copy(isSaving = true, error = null) }
            try {
                useCase.save(draft)
                state.update { it.copy(saved = draft, isJustSaved = true) }
            } finally {
                state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun edit(transform: (StoreState) -> StoreState) {
        state.update { current ->
            val next = transform(current).copy(isJustSaved = false, error = null)
            next.copy(preview = preview(next.saved ?: return@update next, next))
        }
    }

    private fun form(store: Store) = StoreState(
        saved = store,
        name = store.name,
        address = store.address,
        phone = store.phone.formatPhone(),
        latitude = store.latitude.toString(),
        longitude = store.longitude.toString(),
        schedule = store.schedule,
        preview = preview(store, null),
    )

    /** What customers would see with the schedule as it is in the form. */
    private fun preview(store: Store, form: StoreState?): String {
        val schedule = form?.schedule ?: store.schedule
        return "${schedule.summary()} · ${store.copy(schedule = schedule).openingLabel(clock.now())}"
    }
}
