package com.ugg.admin.kit.catalog.presentation.external.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ugg.admin.kit.catalog.presentation.external.view.models.CatalogViewModel
import com.ugg.admin.kit.catalog.presentation.internal.view.CatalogView
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * Tab «Каталог»: price, sewing time, shades in stock and sizes of «Классика». Edits stay in the
 * form, across tab switches too, until «Сохранить».
 *
 * @param contentPadding the tab bar and system bars, from the host's Scaffold.
 */
@Composable
fun CatalogScreen(contentPadding: PaddingValues) {
    val viewModel: CatalogViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()

    CatalogView(
        state = state,
        contentPadding = contentPadding,
        onRetry = { viewModel.fetch() },
        onRefresh = viewModel::refresh,
        onPriceChange = viewModel::onPriceChange,
        onLeadTimeChange = viewModel::onLeadTimeChange,
        onShadeStockChange = viewModel::onShadeStockChange,
        onSizeToggle = viewModel::onSizeToggle,
        onAddShade = viewModel::onAddShade,
        onSave = viewModel::onSave,
    )
}
