package com.ugg.admin.kit.orders.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.data.formatting.toDayAndMonth
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggIconButton
import com.ugg.admin.kit.core.presentation.external.components.UggPhotoPlaceholder
import com.ugg.admin.kit.core.presentation.external.components.UggSection
import com.ugg.admin.kit.core.presentation.external.components.UggTextField
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.orders.domain.states.OrderDetailState

@Composable
internal fun OrderDetailView(
    state: OrderDetailState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onAdvance: () -> Unit,
    onReadyAtChange: (Long?) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCall: (phone: String) -> Unit,
    onWrite: (phone: String) -> Unit,
    onAddPhoto: () -> Unit,
    onCancel: () -> Unit,
) {
    val colors = UggTheme.colors
    val order = state.order

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        TopBar(order = order, onBack = onBack)

        when {
            order != null -> OrderContent(
                order = order,
                state = state,
                onAdvance = onAdvance,
                onReadyAtChange = onReadyAtChange,
                onNoteChange = onNoteChange,
                onSave = onSave,
                onCall = onCall,
                onWrite = onWrite,
                onAddPhoto = onAddPhoto,
                onCancel = onCancel,
            )

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.error?.message ?: "Не удалось загрузить заказ",
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
}

@Composable
private fun TopBar(order: Order?, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = UggTheme.spacing.l, vertical = UggTheme.spacing.s),
        horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UggIconButton(icon = UggIcons.ArrowLeft, contentDescription = "Назад к заказам", onClick = onBack)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = order?.let { "Заказ № ${it.number}" } ?: "Заказ",
                style = UggTheme.typography.displayM,
                color = UggTheme.colors.content,
            )
            if (order != null && order.createdAt.isNotEmpty()) {
                Text(text = "создан ${order.createdAt}", style = UggTheme.typography.bodySmall, color = UggTheme.colors.contentSecondary)
            }
        }
    }
}

@Composable
private fun OrderContent(
    order: Order,
    state: OrderDetailState,
    onAdvance: () -> Unit,
    onReadyAtChange: (Long?) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCall: (String) -> Unit,
    onWrite: (String) -> Unit,
    onAddPhoto: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = UggTheme.spacing.l),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StageSection(order = order, isUpdating = state.isUpdating, error = state.error, onAdvance = onAdvance)
        CustomerSection(order = order, onCall = onCall, onWrite = onWrite)
        ItemsSection(order = order)
        DeliverySection(
            order = order,
            state = state,
            onReadyAtChange = onReadyAtChange,
            onNoteChange = onNoteChange,
            onSave = onSave,
        )
        PhotosSection(photos = order.photos, onAddPhoto = onAddPhoto)

        if (order.status.isActive) {
            Text(
                text = "Отменить заказ",
                style = UggTheme.typography.label,
                color = UggTheme.colors.error,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(UggTheme.shapes.pill)
                    .clickable(enabled = !state.isUpdating, role = Role.Button, onClick = onCancel)
                    .padding(horizontal = UggTheme.spacing.m, vertical = UggTheme.spacing.s),
            )
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        Spacer(Modifier.height(UggTheme.spacing.s))
    }
}

@Composable
private fun StageSection(order: Order, isUpdating: Boolean, error: Throwable?, onAdvance: () -> Unit) {
    val colors = UggTheme.colors
    val reached = when (order.status) {
        OrderStatus.Completed -> OrderStatus.stages.size - 1
        OrderStatus.Cancelled -> -1
        else -> OrderStatus.stages.indexOf(order.status)
    }

    UggSection(title = "Этап") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OrderStatus.stages.forEachIndexed { index, stage ->
                val isReached = index <= reached
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(UggTheme.shapes.pill)
                            .background(if (isReached) colors.accentStrong else colors.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isReached) {
                            Icon(imageVector = UggIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = stage.stageLabel,
                        style = UggTheme.typography.caption.copy(fontWeight = if (isReached) FontWeight.Bold else FontWeight.Medium),
                        color = if (isReached) colors.content else colors.contentSecondary,
                    )
                }
            }
        }

        val advanceLabel = order.status.advanceLabel
        if (advanceLabel != null) {
            UggButton(
                text = advanceLabel,
                onClick = onAdvance,
                enabled = !isUpdating,
                showArrow = false,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            )
            Text(
                text = "Клиент увидит новый этап в разделе «Мой заказ»",
                style = UggTheme.typography.caption,
                color = colors.contentSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                text = if (order.status == OrderStatus.Cancelled) "Заказ отменён" else "Заказ выдан клиенту",
                style = UggTheme.typography.label,
                color = if (order.status == OrderStatus.Cancelled) colors.error else colors.info,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (error != null) {
            Text(
                text = error.message ?: "Не получилось, попробуйте ещё раз",
                style = UggTheme.typography.caption,
                color = colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CustomerSection(order: Order, onCall: (String) -> Unit, onWrite: (String) -> Unit) {
    val colors = UggTheme.colors
    val customer = order.customer

    UggSection(title = "Клиент") {
        Text(text = customer.name, style = UggTheme.typography.heading, color = colors.content)
        Text(
            text = listOf(customer.phone, customer.size).filter { it.isNotEmpty() }.joinToString(" · "),
            style = UggTheme.typography.bodySmall,
            color = colors.contentSecondary,
        )
        if (customer.phoneDial.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.xs)) {
                UggButton(
                    text = "Позвонить",
                    onClick = { onCall(customer.phoneDial) },
                    style = UggButtonStyle.Secondary,
                    showArrow = false,
                    leadingIcon = UggIcons.Phone,
                    modifier = Modifier.weight(1f),
                )
                UggButton(
                    text = "Написать",
                    onClick = { onWrite(customer.phoneDial) },
                    style = UggButtonStyle.Secondary,
                    showArrow = false,
                    leadingIcon = UggIcons.Message,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ItemsSection(order: Order) {
    val colors = UggTheme.colors

    UggSection(title = "Состав") {
        order.boots?.let { boots ->
            Row(horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(UggTheme.shapes.small)
                        .background(Color(boots.color)),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "${boots.title} × ${boots.quantity}", style = UggTheme.typography.label, color = colors.content)
                    Text(text = boots.options, style = UggTheme.typography.caption, color = colors.contentSecondary)
                }
                Text(text = boots.price, style = UggTheme.typography.label.copy(fontWeight = FontWeight.Medium), color = colors.content)
            }
        }
        order.extras.forEach { line ->
            Row(horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.xs)) {
                Text(text = line.title, style = UggTheme.typography.bodySmall, color = colors.contentSecondary, modifier = Modifier.weight(1f))
                Text(text = line.price, style = UggTheme.typography.label.copy(fontWeight = FontWeight.Medium), color = colors.content)
            }
        }
        HorizontalDivider(thickness = 1.dp, color = colors.outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Итого", style = UggTheme.typography.heading, color = colors.content, modifier = Modifier.weight(1f))
            Text(text = order.total, style = UggTheme.typography.heading.copy(fontWeight = FontWeight.Bold), color = colors.content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliverySection(
    order: Order,
    state: OrderDetailState,
    onReadyAtChange: (Long?) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.medium
    var isPickingDate by rememberSaveable { mutableStateOf(false) }

    UggSection(title = "Доставка и срок") {
        Text(text = order.delivery.full, style = UggTheme.typography.body, color = colors.content)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Будет готова к", style = UggTheme.typography.caption, color = colors.contentSecondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .clip(shape)
                    .background(colors.background)
                    .border(1.dp, colors.outline, shape)
                    .clickable(enabled = order.status.isActive, role = Role.Button) { isPickingDate = true }
                    .padding(horizontal = UggTheme.spacing.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.readyAt?.toDayAndMonth() ?: "Не назначена",
                    style = UggTheme.typography.body,
                    color = if (state.readyAt != null) colors.content else colors.contentSecondary,
                    modifier = Modifier.weight(1f),
                )
                Icon(imageVector = UggIcons.Clock, contentDescription = null, tint = colors.contentSecondary, modifier = Modifier.size(18.dp))
            }
        }

        UggTextField(
            value = state.note,
            onValueChange = onNoteChange,
            label = "Заметка мастера, видна только вам",
            placeholder = "Например: мех погуще на пятку",
            singleLine = false,
        )

        if (state.hasChanges) {
            UggButton(
                text = if (state.isSaving) "Сохраняем…" else "Сохранить",
                onClick = onSave,
                enabled = !state.isSaving,
                showArrow = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (isPickingDate) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.readyAt)

        DatePickerDialog(
            onDismissRequest = { isPickingDate = false },
            confirmButton = {
                TextButton(onClick = {
                    isPickingDate = false
                    onReadyAtChange(pickerState.selectedDateMillis)
                }) { Text("Готово") }
            },
            dismissButton = {
                TextButton(onClick = { isPickingDate = false }) { Text("Отмена") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun PhotosSection(photos: List<String>, onAddPhoto: () -> Unit) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.small
    val tiles: List<String?> = photos + null // null is the «+ Добавить» tile

    UggSection(title = "Фото процесса для клиента") {
        tiles.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.xs)) {
                row.forEach { photo ->
                    val tileModifier = Modifier.weight(1f).height(84.dp)
                    if (photo != null) {
                        // TODO: an image loader once photos live in Firebase Storage.
                        UggPhotoPlaceholder(modifier = tileModifier, shape = shape)
                    } else {
                        Box(
                            modifier = tileModifier
                                .clip(shape)
                                .background(colors.background)
                                .border(1.dp, colors.accent, shape)
                                .clickable(role = Role.Button, onClick = onAddPhoto),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "+ Добавить", style = UggTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = colors.accentStrong)
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
