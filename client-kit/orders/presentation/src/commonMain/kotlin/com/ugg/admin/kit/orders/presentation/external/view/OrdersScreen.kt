package com.ugg.admin.kit.orders.presentation.external.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ugg.admin.kit.orders.presentation.external.view.models.OrdersViewModel
import com.ugg.admin.kit.orders.presentation.internal.view.OrdersView
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * Tab «Заказы»: today's summary, status chips and the live list.
 *
 * @param contentPadding the tab bar and system bars, from the host's Scaffold.
 */
@Composable
fun OrdersScreen(
    contentPadding: PaddingValues,
    onOpenOrder: (orderId: String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val viewModel: OrdersViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()

    OrdersView(
        state = state,
        contentPadding = contentPadding,
        onFilter = viewModel::onFilter,
        onToggleSearch = viewModel::onToggleSearch,
        onQuery = viewModel::onQuery,
        onOpenOrder = onOpenOrder,
        onOpenSettings = onOpenSettings,
        onRetry = viewModel::fetch,
    )
}
