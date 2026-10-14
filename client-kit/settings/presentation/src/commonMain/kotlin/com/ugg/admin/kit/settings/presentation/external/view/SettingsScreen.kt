package com.ugg.admin.kit.settings.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ugg.admin.kit.core.presentation.external.components.UggAlertDialog
import com.ugg.admin.kit.settings.presentation.external.view.models.SettingsViewModel
import com.ugg.admin.kit.settings.presentation.internal.view.SettingsView
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * The signed-in account and «Выйти».
 *
 * @param onSignOut ends the session; the splash then returns the app to the phone step.
 */
@Composable
fun SettingsScreen(onBack: () -> Unit, onSignOut: () -> Unit) {
    val viewModel: SettingsViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()
    var isConfirmingSignOut by rememberSaveable { mutableStateOf(false) }

    SettingsView(
        state = state,
        onBack = onBack,
        onSignOut = { isConfirmingSignOut = true },
    )

    if (isConfirmingSignOut) {
        UggAlertDialog(
            title = "Выйти из аккаунта?",
            text = "Чтобы вернуться, понадобится код из SMS.",
            confirmText = "Выйти",
            dismissText = "Остаться",
            onConfirm = {
                isConfirmingSignOut = false
                viewModel.onSignOutStarted()
                onSignOut()
            },
            onDismiss = { isConfirmingSignOut = false },
        )
    }
}
