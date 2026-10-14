package com.ugg.admin.kit.stores.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggSection
import com.ugg.admin.kit.core.presentation.external.components.UggSwitch
import com.ugg.admin.kit.core.presentation.external.components.UggTextField
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.stores.domain.entities.StoreDay
import com.ugg.admin.kit.stores.domain.entities.full
import com.ugg.admin.kit.stores.domain.entities.short
import com.ugg.admin.kit.stores.domain.states.StoreState
import kotlinx.datetime.DayOfWeek

@Composable
internal fun StoreView(
    state: StoreState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onOpenMap: (latitude: Double, longitude: Double) -> Unit,
    onWorkingChange: (DayOfWeek, Boolean) -> Unit,
    onHoursChange: (DayOfWeek, open: String, close: String) -> Unit,
    onSave: () -> Unit,
) {
    val colors = UggTheme.colors
    var editingDay by rememberSaveable { mutableStateOf<DayOfWeek?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        BackHeader(kicker = "Точки", title = state.saved?.city ?: "Точка", onBack = onBack)

        when {
            state.saved != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ContactsSection(
                    state = state,
                    onNameChange = onNameChange,
                    onAddressChange = onAddressChange,
                    onPhoneChange = onPhoneChange,
                    onLatitudeChange = onLatitudeChange,
                    onLongitudeChange = onLongitudeChange,
                    onOpenMap = onOpenMap,
                )
                ScheduleSection(
                    schedule = state.schedule,
                    onWorkingChange = onWorkingChange,
                    onEditHours = { editingDay = it },
                )
                Preview(text = state.preview)
                UggButton(
                    text = when {
                        state.isSaving -> "Сохраняем…"
                        state.isJustSaved && !state.hasChanges -> "Сохранено"
                        else -> "Сохранить"
                    },
                    onClick = onSave,
                    enabled = state.canSave,
                    showArrow = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                state.error?.let { error ->
                    Text(
                        text = error.message ?: "Не получилось сохранить, попробуйте ещё раз",
                        style = UggTheme.typography.caption,
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.error?.message ?: "Не удалось загрузить точку",
                    style = UggTheme.typography.body,
                    color = colors.contentSecondary,
                    textAlign = TextAlign.Center,
                )
                UggButton(text = "Повторить", onClick = onRetry, style = UggButtonStyle.Secondary, showArrow = false)
            }

            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.accent)
            }
        }
    }

    val day = editingDay?.let { selected -> state.schedule.firstOrNull { it.day == selected } }
    if (day != null) {
        HoursDialog(
            day = day,
            onConfirm = { open, close ->
                editingDay = null
                onHoursChange(day.day, open, close)
            },
            onDismiss = { editingDay = null },
        )
    }
}

@Composable
private fun ContactsSection(
    state: StoreState,
    onNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onOpenMap: (Double, Double) -> Unit,
) {
    val colors = UggTheme.colors
    var isMapOpen by rememberSaveable { mutableStateOf(false) }
    val showCoordinates = isMapOpen || state.coordinatesError != null

    UggSection(title = "Контакты") {
        UggTextField(value = state.name, onValueChange = onNameChange, label = "Название", error = state.nameError)
        UggTextField(value = state.address, onValueChange = onAddressChange, label = "Адрес", error = state.addressError)
        UggTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            label = "Телефон",
            error = state.phoneError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )

        if (showCoordinates) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                UggTextField(
                    value = state.latitude,
                    onValueChange = onLatitudeChange,
                    label = "Широта",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )
                UggTextField(
                    value = state.longitude,
                    onValueChange = onLongitudeChange,
                    label = "Долгота",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = state.coordinatesError ?: "Пин на карте у клиентов. Проверьте точку на Яндекс Картах.",
                style = UggTheme.typography.caption,
                color = if (state.coordinatesError != null) colors.error else colors.contentSecondary,
            )
        }

        val latitude = state.parsedLatitude
        val longitude = state.parsedLongitude
        LinkButton(
            text = when {
                !showCoordinates -> "Уточнить точку на карте"
                latitude != null && longitude != null -> "Открыть на Яндекс Картах"
                else -> null
            },
            onClick = {
                if (!showCoordinates) {
                    isMapOpen = true
                } else if (latitude != null && longitude != null) {
                    onOpenMap(latitude, longitude)
                }
            },
        )
    }
}

@Composable
private fun LinkButton(text: String?, onClick: () -> Unit) {
    if (text == null) return
    Text(
        text = text,
        style = UggTheme.typography.label,
        color = UggTheme.colors.accentStrong,
        modifier = Modifier
            .clip(UggTheme.shapes.pill)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = UggTheme.spacing.xs),
    )
}

@Composable
private fun ScheduleSection(
    schedule: List<StoreDay>,
    onWorkingChange: (DayOfWeek, Boolean) -> Unit,
    onEditHours: (DayOfWeek) -> Unit,
) {
    val colors = UggTheme.colors

    UggSection(title = "График · время иркутское") {
        Column {
            schedule.forEachIndexed { index, day ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.outline)
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = day.day.short,
                        style = UggTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                        color = colors.content,
                        modifier = Modifier.width(28.dp),
                    )
                    if (day.isWorking) {
                        Text(
                            text = "${day.open} – ${day.close}",
                            style = UggTheme.typography.label,
                            color = colors.content,
                            modifier = Modifier
                                .weight(1f)
                                .clip(UggTheme.shapes.small)
                                .clickable(role = Role.Button, onClickLabel = "Изменить часы") { onEditHours(day.day) }
                                .padding(vertical = UggTheme.spacing.xs),
                        )
                    } else {
                        Text(
                            text = "выходной",
                            style = UggTheme.typography.bodySmall,
                            color = colors.contentSecondary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    UggSwitch(
                        checked = day.isWorking,
                        onCheckedChange = { onWorkingChange(day.day, it) },
                        modifier = Modifier.semantics { contentDescription = "${day.day.full} — рабочий день" },
                    )
                }
            }
        }
        Text(
            text = "Нажмите на часы, чтобы изменить их",
            style = UggTheme.typography.caption,
            color = colors.contentSecondary,
        )
    }
}

@Composable
private fun Preview(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(UggTheme.shapes.medium)
            .background(UggTheme.colors.surface)
            .padding(horizontal = UggTheme.spacing.m, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = "Так клиенты увидят точку сейчас", style = UggTheme.typography.caption, color = UggTheme.colors.contentSecondary)
        Text(text = text, style = UggTheme.typography.label, color = UggTheme.colors.content)
    }
}
