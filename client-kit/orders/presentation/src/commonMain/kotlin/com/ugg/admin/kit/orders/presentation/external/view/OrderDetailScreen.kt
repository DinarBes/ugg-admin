package com.ugg.admin.kit.orders.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import com.ugg.admin.kit.core.presentation.external.components.UggAlertDialog
import com.ugg.admin.kit.orders.presentation.external.view.models.OrderDetailViewModel
import com.ugg.admin.kit.orders.presentation.internal.view.OrderDetailView
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * One order: stage, customer, items, deadline and the master's note. Cancelling asks first and
 * then returns to the list ([onBack]).
 *
 * @param onAddPhoto «+ Добавить» in «Фото процесса»; uploading needs Firebase Storage, so the host decides.
 */
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    onAddPhoto: () -> Unit = {},
) {
    val viewModel: OrderDetailViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current
    var isConfirmingCancel by rememberSaveable { mutableStateOf(false) }

    // A fresh card on every visit, but not on rotation: the flag survives it, a new visit starts without it.
    var isStarted by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(orderId) {
        if (!isStarted) {
            isStarted = true
            viewModel.load(orderId)
        }
    }

    // Cancelled from here: nothing is left to do on the card, so back to the orders list.
    LaunchedEffect(state.isCancelled) {
        if (state.isCancelled) {
            viewModel.onCancelHandled()
            onBack()
        }
    }

    OrderDetailView(
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
        onAdvance = viewModel::onAdvance,
        onReadyAtChange = viewModel::onReadyAtChange,
        onNoteChange = viewModel::onNoteChange,
        onSave = viewModel::onSave,
        onCall = { phone -> runCatching { uriHandler.openUri("tel:$phone") } },
        onWrite = { phone -> runCatching { uriHandler.openUri("sms:$phone") } },
        onAddPhoto = onAddPhoto,
        onCancel = { isConfirmingCancel = true },
    )

    if (isConfirmingCancel) {
        UggAlertDialog(
            title = "Отменить заказ?",
            text = state.order?.let { "№ ${it.number} · ${it.customer.name}. Клиент увидит, что заказ отменён." }.orEmpty(),
            confirmText = "Отменить заказ",
            dismissText = "Оставить",
            onConfirm = {
                isConfirmingCancel = false
                viewModel.onCancel()
            },
            onDismiss = { isConfirmingCancel = false },
        )
    }
}
