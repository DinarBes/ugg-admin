package com.ugg.admin.kit.stores.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.stores.domain.entities.StoreDay
import com.ugg.admin.kit.stores.domain.entities.full
import com.ugg.admin.kit.stores.domain.entities.toLocalTimeOrNull

/** Opening and closing time of one day, 24-hour, typed in. Closing must come after opening. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HoursDialog(
    day: StoreDay,
    onConfirm: (open: String, close: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = UggTheme.colors
    val openTime = day.open.toLocalTimeOrNull()
    val closeTime = day.close.toLocalTimeOrNull()
    val open = rememberTimePickerState(initialHour = openTime?.hour ?: 10, initialMinute = openTime?.minute ?: 0, is24Hour = true)
    val close = rememberTimePickerState(initialHour = closeTime?.hour ?: 20, initialMinute = closeTime?.minute ?: 0, is24Hour = true)
    val isValid = close.hour * 60 + close.minute > open.hour * 60 + open.minute

    val pickerColors = TimePickerDefaults.colors(
        timeSelectorSelectedContainerColor = colors.surface,
        timeSelectorSelectedContentColor = colors.content,
        timeSelectorUnselectedContainerColor = colors.background,
        timeSelectorUnselectedContentColor = colors.content,
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(UggTheme.shapes.large)
                .background(colors.surfaceElevated)
                .padding(UggTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = day.day.full, style = UggTheme.typography.heading, color = colors.content)

            Text(text = "Открытие", style = UggTheme.typography.caption, color = colors.contentSecondary)
            TimeInput(state = open, colors = pickerColors)

            Text(text = "Закрытие", style = UggTheme.typography.caption, color = colors.contentSecondary)
            TimeInput(state = close, colors = pickerColors)

            if (!isValid) {
                Text(
                    text = "Закрытие должно быть позже открытия",
                    style = UggTheme.typography.caption,
                    color = colors.error,
                    textAlign = TextAlign.Center,
                )
            }

            UggButton(
                text = "Готово",
                onClick = { onConfirm(open.label(), close.label()) },
                enabled = isValid,
                showArrow = false,
                modifier = Modifier.fillMaxWidth(),
            )
            UggButton(
                text = "Отмена",
                onClick = onDismiss,
                style = UggButtonStyle.Secondary,
                showArrow = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun TimePickerState.label(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
