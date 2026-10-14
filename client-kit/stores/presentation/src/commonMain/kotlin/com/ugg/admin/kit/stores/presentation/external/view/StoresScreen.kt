package com.ugg.admin.kit.stores.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ugg.admin.kit.stores.presentation.external.view.models.StoresViewModel
import com.ugg.admin.kit.stores.presentation.internal.view.StoresView
import org.kodein.di.compose.viewmodel.rememberViewModel

/** «Точки»: both stores with the hours customers see; a tap opens the store form. */
@Composable
fun StoresScreen(
    onBack: () -> Unit,
    onOpenStore: (storeId: String) -> Unit,
) {
    val viewModel: StoresViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()

    StoresView(
        state = state,
        onBack = onBack,
        onRetry = viewModel::fetch,
        onOpenStore = onOpenStore,
    )
}
