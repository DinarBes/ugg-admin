package com.ugg.admin.kit.orders.presentation.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/** The badge on a list card. */
internal val OrderStatus.badgeLabel: String
    get() = when (this) {
        OrderStatus.Accepted -> "Новый"
        OrderStatus.Cutting -> "Раскрой"
        OrderStatus.Sewing -> "Пошив"
        OrderStatus.Ready -> "Готов"
        OrderStatus.Completed -> "Выдан"
        OrderStatus.Cancelled -> "Отменён"
    }

/** A step of the stepper on the order card. */
internal val OrderStatus.stageLabel: String
    get() = when (this) {
        OrderStatus.Accepted -> "Принят"
        else -> badgeLabel
    }

/** The main button of the order card; `null` once the order is closed. */
internal val OrderStatus.advanceLabel: String?
    get() = when (this) {
        OrderStatus.Accepted -> "Взять в работу → Раскрой"
        OrderStatus.Cutting -> "Раскрой готов → Пошив"
        OrderStatus.Sewing -> "Пошив готов → Готов"
        OrderStatus.Ready -> "Выдать клиенту"
        OrderStatus.Completed, OrderStatus.Cancelled -> null
    }

/** Container and content of the status badge. */
@Composable
@ReadOnlyComposable
internal fun OrderStatus.badgeColors(): Pair<Color, Color> {
    val colors = UggTheme.colors
    return when (this) {
        OrderStatus.Accepted -> colors.attentionContainer to colors.onAttentionContainer
        OrderStatus.Cutting, OrderStatus.Sewing -> colors.progressContainer to colors.onProgressContainer
        OrderStatus.Ready -> colors.infoContainer to colors.info
        OrderStatus.Completed, OrderStatus.Cancelled -> colors.surface to colors.contentSecondary
    }
}
