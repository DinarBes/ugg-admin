package com.ugg.admin.kit.clients.presentation.external.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ugg.admin.kit.clients.presentation.external.view.models.ClientsViewModel
import com.ugg.admin.kit.clients.presentation.internal.view.ClientsView
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * Tab «Клиенты»: everyone who signed in, with size and their current order.
 *
 * @param onOpenOrder a tap on a client with orders opens the current one, or the latest.
 */
@Composable
fun ClientsScreen(
    contentPadding: PaddingValues,
    onOpenOrder: (orderId: String) -> Unit,
) {
    val viewModel: ClientsViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()

    ClientsView(
        state = state,
        contentPadding = contentPadding,
        onQuery = viewModel::onQuery,
        onRetry = viewModel::fetch,
        onOpenOrder = onOpenOrder,
    )
}
