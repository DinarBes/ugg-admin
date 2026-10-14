package com.ugg.admin.kit.catalog.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ugg.admin.kit.core.data.formatting.toArgb
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggTextField
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

private val hexPattern = Regex("^#?[0-9A-Fa-f]{6}$")

/** «+ Добавить цвет»: a name and a hex color, with a live swatch. */
@Composable
internal fun AddShadeDialog(
    takenNames: List<String>,
    onAdd: (name: String, color: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = UggTheme.colors
    var name by rememberSaveable { mutableStateOf("") }
    var hex by rememberSaveable { mutableStateOf("#") }

    val trimmedName = name.trim()
    val isNameTaken = takenNames.any { it.equals(trimmedName, ignoreCase = true) }
    val isHexValid = hexPattern.matches(hex.trim())
    val canAdd = trimmedName.isNotEmpty() && !isNameTaken && isHexValid

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(UggTheme.shapes.large)
                .background(colors.surfaceElevated)
                .padding(UggTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
        ) {
            Text(
                text = "Новый цвет",
                style = UggTheme.typography.heading,
                color = colors.content,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            UggTextField(
                value = name,
                onValueChange = { name = it.take(24) },
                label = "Название",
                placeholder = "Например, Мокко",
                error = if (isNameTaken) "Такой цвет уже есть" else null,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s), verticalAlignment = Alignment.CenterVertically) {
                UggTextField(
                    value = hex,
                    onValueChange = { hex = it.take(7) },
                    label = "Цвет в HEX",
                    placeholder = "#B98A5E",
                    error = if (hex.length >= 7 && !isHexValid) "Шесть знаков: 0–9, A–F" else null,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    modifier = Modifier.weight(1f),
                )
                ShadeSwatch(
                    color = if (isHexValid) Color(hex.trim().toArgb()) else colors.surface,
                    modifier = Modifier.padding(top = 20.dp),
                )
            }

            Text(
                text = "Цвет появится в наличии после «Сохранить»",
                style = UggTheme.typography.caption,
                color = colors.contentSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            UggButton(
                text = "Добавить",
                onClick = { onAdd(trimmedName, hex.trim().toArgb()) },
                enabled = canAdd,
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
