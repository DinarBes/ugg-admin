package com.ugg.admin.kit.stores.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import com.ugg.admin.kit.stores.presentation.external.view.models.StoreViewModel
import com.ugg.admin.kit.stores.presentation.internal.view.StoreView
import org.kodein.di.compose.viewmodel.rememberViewModel

/** One store: contacts, the point on the map and the weekly schedule, saved together. */
@Composable
fun StoreScreen(storeId: String, onBack: () -> Unit) {
    val viewModel: StoreViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    // A fresh form on every visit, but not on rotation: the flag survives it, a new visit starts without it.
    var isStarted by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(storeId) {
        if (!isStarted) {
            isStarted = true
            viewModel.load(storeId)
        }
    }

    StoreView(
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
        onNameChange = viewModel::onNameChange,
        onAddressChange = viewModel::onAddressChange,
        onPhoneChange = viewModel::onPhoneChange,
        onLatitudeChange = viewModel::onLatitudeChange,
        onLongitudeChange = viewModel::onLongitudeChange,
        onOpenMap = { latitude, longitude ->
            runCatching { uriHandler.openUri("https://yandex.ru/maps/?pt=$longitude,$latitude&z=17&l=map") }
        },
        onWorkingChange = viewModel::onWorkingChange,
        onHoursChange = viewModel::onHoursChange,
        onSave = viewModel::onSave,
    )
}
