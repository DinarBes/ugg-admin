package com.ugg.admin.kit.catalog.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.catalog.domain.entities.CatalogShade
import com.ugg.admin.kit.catalog.domain.entities.CatalogSize
import com.ugg.admin.kit.catalog.domain.states.CatalogState
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggPullToRefreshBox
import com.ugg.admin.kit.core.presentation.external.components.UggSection
import com.ugg.admin.kit.core.presentation.external.components.UggSwitch
import com.ugg.admin.kit.core.presentation.external.components.UggTextField
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

private const val SIZE_COLUMNS = 6

@Composable
internal fun CatalogView(
    state: CatalogState,
    contentPadding: PaddingValues,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onPriceChange: (String) -> Unit,
    onLeadTimeChange: (String) -> Unit,
    onShadeStockChange: (shadeId: String, inStock: Boolean) -> Unit,
    onSizeToggle: (label: String) -> Unit,
    onAddShade: (name: String, color: Long) -> Unit,
    onSave: () -> Unit,
) {
    val colors = UggTheme.colors
    var isAddingShade by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        when {
            state.saved != null -> UggPullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                indicatorPadding = PaddingValues(top = contentPadding.calculateTopPadding()),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = contentPadding.calculateTopPadding() + UggTheme.spacing.xl,
                            bottom = contentPadding.calculateBottomPadding() + UggTheme.spacing.l,
                        )
                        .padding(horizontal = UggTheme.spacing.l),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Header(title = state.saved?.title.orEmpty())
                    PriceSection(state = state, onPriceChange = onPriceChange, onLeadTimeChange = onLeadTimeChange)
                    ShadesSection(shades = state.shades, onStockChange = onShadeStockChange, onAdd = { isAddingShade = true })
                    SizesSection(sizes = state.sizes, onToggle = onSizeToggle)
                    SaveButton(state = state, onSave = onSave)

                    state.error?.let { error ->
                        Text(
                            text = error.message ?: "Не получилось сохранить, попробуйте ещё раз",
                            style = UggTheme.typography.caption,
                            color = colors.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding).padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.error?.message ?: "Не удалось загрузить каталог",
                    style = UggTheme.typography.body,
                    color = colors.contentSecondary,
                    textAlign = TextAlign.Center,
                )
                UggButton(text = "Повторить", onClick = onRetry, style = UggButtonStyle.Secondary, showArrow = false)
            }

            else -> Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.accent)
            }
        }
    }

    if (isAddingShade) {
        AddShadeDialog(
            takenNames = state.shades.map { it.name },
            onAdd = { name, color ->
                isAddingShade = false
                onAddShade(name, color)
            },
            onDismiss = { isAddingShade = false },
        )
    }
}

@Composable
private fun Header(title: String) {
    val colors = UggTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = "Единственная модель".uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)
        Text(text = title, style = UggTheme.typography.displayL.copy(fontWeight = FontWeight.SemiBold), color = colors.content)
        Text(text = "Клиенты видят изменения сразу после сохранения", style = UggTheme.typography.bodySmall, color = colors.contentSecondary)
    }
}

@Composable
private fun PriceSection(state: CatalogState, onPriceChange: (String) -> Unit, onLeadTimeChange: (String) -> Unit) {
    UggSection(title = "Цена и срок") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UggTextField(
                value = state.price,
                onValueChange = onPriceChange,
                label = "Цена, ₽",
                error = state.priceError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
            UggTextField(
                value = state.leadTime,
                onValueChange = onLeadTimeChange,
                label = "Пошив, дней",
                placeholder = "5–7",
                error = state.leadTimeError,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ShadesSection(
    shades: List<CatalogShade>,
    onStockChange: (String, Boolean) -> Unit,
    onAdd: () -> Unit,
) {
    val colors = UggTheme.colors

    UggSection(title = "Цвета", trailing = "в наличии") {
        Column {
            shades.forEachIndexed { index, shade ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.outline)
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShadeSwatch(color = Color(shade.color))
                    Text(
                        text = shade.name,
                        style = UggTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                        color = if (shade.inStock) colors.content else colors.contentSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    UggSwitch(
                        checked = shade.inStock,
                        onCheckedChange = { onStockChange(shade.id, it) },
                        modifier = Modifier.semantics { contentDescription = "${shade.name} в наличии" },
                    )
                }
            }
        }
        Text(
            text = "+ Добавить цвет",
            style = UggTheme.typography.label,
            color = colors.accentStrong,
            modifier = Modifier
                .clip(UggTheme.shapes.pill)
                .clickable(role = Role.Button, onClick = onAdd)
                .padding(vertical = UggTheme.spacing.xs),
        )
    }
}

/** The shade circle; light shades get an outline so they don't melt into the white card. */
@Composable
internal fun ShadeSwatch(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(UggTheme.shapes.pill)
            .background(color)
            .border(1.dp, UggTheme.colors.outline, UggTheme.shapes.pill),
    )
}

@Composable
private fun SizesSection(sizes: List<CatalogSize>, onToggle: (String) -> Unit) {
    UggSection(title = "Размеры") {
        Text(
            text = "Нажмите на размер, чтобы скрыть его у клиентов — например, когда нет кроя",
            style = UggTheme.typography.caption,
            color = UggTheme.colors.contentSecondary,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            sizes.chunked(SIZE_COLUMNS).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { size ->
                        SizeCell(size = size, onClick = { onToggle(size.label) }, modifier = Modifier.weight(1f))
                    }
                    repeat(SIZE_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun SizeCell(size: CatalogSize, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.small
    val dashColor = colors.accent

    val outline = if (size.isAvailable) {
        Modifier.border(1.dp, colors.outline, shape)
    } else {
        Modifier.drawBehind {
            val stroke = 1.dp.toPx()
            drawRoundRect(
                color = dashColor,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(this.size.width - stroke, this.size.height - stroke),
                cornerRadius = CornerRadius(14.dp.toPx()),
                style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))),
            )
        }
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(if (size.isAvailable) colors.background else colors.surface)
            .then(outline)
            .clickable(role = Role.Checkbox, onClick = onClick)
            .semantics { contentDescription = if (size.isAvailable) "Размер ${size.label}" else "Размер ${size.label} — скрыт" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = size.label,
            style = UggTheme.typography.label.copy(
                textDecoration = if (size.isAvailable) null else TextDecoration.LineThrough,
            ),
            color = if (size.isAvailable) colors.content else colors.contentSecondary,
        )
    }
}

@Composable
private fun SaveButton(state: CatalogState, onSave: () -> Unit) {
    val text = when {
        state.isSaving -> "Сохраняем…"
        state.isJustSaved && !state.hasChanges -> "Сохранено"
        else -> "Сохранить"
    }

    UggButton(
        text = text,
        onClick = onSave,
        enabled = state.canSave,
        showArrow = false,
        modifier = Modifier.fillMaxWidth(),
    )
}
