package com.ugg.admin.kit.catalog.presentation.external.view.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ugg.admin.kit.catalog.domain.entities.Catalog
import com.ugg.admin.kit.catalog.domain.entities.CatalogShade
import com.ugg.admin.kit.catalog.domain.states.CatalogState
import com.ugg.admin.kit.catalog.domain.useCases.CatalogUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val useCase: CatalogUseCase,
) : ViewModel() {
    val state: StateFlow<CatalogState>
        field = MutableStateFlow(CatalogState())

    private val unexpectedExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { this@CatalogViewModel::class.toString() }

        state.update { it.copy(error = throwable, isLoading = false, isRefreshing = false, isSaving = false) }
    }

    init {
        fetch()
    }

    fun fetch() = viewModelScope.launch(unexpectedExceptionHandler) {
        state.update { it.copy(isLoading = true, error = null) }

        val catalog = useCase.fetch()
        state.update { it.loaded(catalog, replaceForm = true) }
    }

    /** Pull-to-refresh. Unsaved edits survive it: only the baseline they are compared with moves. */
    fun refresh() {
        if (state.value.isRefreshing) return
        state.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch(unexpectedExceptionHandler) {
            try {
                val catalog = useCase.fetch()
                state.update { it.loaded(catalog, replaceForm = !it.hasChanges) }
            } finally {
                state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onPriceChange(price: String) = edit {
        it.copy(price = price.filter { c -> c.isDigit() || c == ' ' }.take(9))
    }

    fun onLeadTimeChange(leadTime: String) = edit {
        it.copy(leadTime = leadTime.take(9))
    }

    fun onShadeStockChange(shadeId: String, inStock: Boolean) = edit { state ->
        state.copy(shades = state.shades.map { if (it.id == shadeId) it.copy(inStock = inStock) else it })
    }

    fun onSizeToggle(label: String) = edit { state ->
        state.copy(sizes = state.sizes.map { if (it.label == label) it.copy(isAvailable = !it.isAvailable) else it })
    }

    /** A new shade starts in stock; it reaches customers with «Сохранить». */
    fun onAddShade(name: String, color: Long) = edit { state ->
        val shade = CatalogShade(
            id = CatalogShade.idFor(name, state.shades.map { it.id }),
            name = name.trim(),
            color = color,
            inStock = true,
        )
        state.copy(shades = state.shades + shade)
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

    private fun edit(transform: (CatalogState) -> CatalogState) {
        state.update { transform(it).copy(isJustSaved = false, error = null) }
    }

    private fun CatalogState.loaded(catalog: Catalog, replaceForm: Boolean): CatalogState {
        val base = copy(saved = catalog, isLoading = false, error = null)
        if (!replaceForm) return base

        return base.copy(
            price = catalog.price.toString(),
            leadTime = catalog.leadTime.label,
            shades = catalog.shades,
            sizes = catalog.sizes,
        )
    }
}
