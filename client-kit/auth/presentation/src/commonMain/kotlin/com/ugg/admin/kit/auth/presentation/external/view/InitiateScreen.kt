package com.ugg.admin.kit.auth.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ugg.admin.kit.auth.presentation.external.view.models.InitiateViewModel
import com.ugg.admin.kit.auth.presentation.internal.view.InitiateView
import org.kodein.di.compose.viewmodel.rememberViewModel

/** Russian numbers only for now: the user types the ten digits after «+7». */
private const val COUNTRY_CODE = "+7"

/** Phone number step of the sign-in. The admin app has nothing to show a guest, so there is no way around it. */
@Composable
fun InitiateScreen() {
    val viewModel: InitiateViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()

    InitiateView(
        // The state keeps the full international number the backend expects; the field edits
        // only the national part.
        nationalNumber = state.phone.removePrefix(COUNTRY_CODE),
        countryCode = COUNTRY_CODE,
        error = state.error,
        isLoading = state.isLoading,
        onNationalNumberChanged = { digits ->
            viewModel.onStateChange(state.copy(phone = if (digits.isEmpty()) "" else COUNTRY_CODE + digits, error = null))
        },
        onSubmit = { viewModel.onSubmit() },
    )
}
