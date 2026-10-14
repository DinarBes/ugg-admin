package com.ugg.admin.kit.orders.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ugg.admin.kit.core.data.formatting.plural
import com.ugg.admin.kit.core.data.formatting.toDayAndMonth
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggIconButton
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.orders.domain.entities.Order
import com.ugg.admin.kit.orders.domain.entities.OrderFilter
import com.ugg.admin.kit.orders.domain.entities.OrderStatus
import com.ugg.admin.kit.orders.domain.states.OrdersState

@Composable
internal fun OrdersView(
    state: OrdersState,
    contentPadding: PaddingValues,
    onFilter: (OrderFilter) -> Unit,
    onToggleSearch: () -> Unit,
    onQuery: (String) -> Unit,
    onOpenOrder: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val colors = UggTheme.colors
    val horizontal = UggTheme.spacing.l
    val orders = state.visibleOrders

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.background),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + UggTheme.spacing.xl,
            bottom = contentPadding.calculateBottomPadding() + UggTheme.spacing.l,
        ),
        verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m),
    ) {
        item(key = "header") {
            Header(
                today = state.today,
                isSearching = state.isSearching,
                onToggleSearch = onToggleSearch,
                modifier = Modifier.padding(horizontal = horizontal),
            )
        }

        if (state.isSearching) {
            item(key = "search") {
                SearchField(query = state.query, onQuery = onQuery, modifier = Modifier.padding(horizontal = horizontal))
            }
        } else {
            item(key = "summary") {
                Summary(state = state, onFilter = onFilter, modifier = Modifier.padding(horizontal = horizontal))
            }
            item(key = "filters") {
                Filters(state = state, onFilter = onFilter, horizontal = horizontal)
            }
        }

        when {
            state.orders.isEmpty() && state.error != null -> item(key = "error") {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = horizontal, vertical = UggTheme.spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.error?.message ?: "Не удалось загрузить заказы",
                        style = UggTheme.typography.body,
                        color = colors.contentSecondary,
                        textAlign = TextAlign.Center,
                    )
                    UggButton(text = "Повторить", onClick = onRetry, style = UggButtonStyle.Secondary, showArrow = false)
                }
            }

            state.orders.isEmpty() && state.isLoading -> item(key = "loading") {
                Box(Modifier.fillMaxWidth().padding(vertical = UggTheme.spacing.xl), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.accent)
                }
            }

            orders.isEmpty() -> item(key = "empty") {
                Text(
                    text = if (state.isSearching) "Ничего не нашлось" else state.filter.emptyText,
                    style = UggTheme.typography.body,
                    color = colors.contentSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = horizontal, vertical = UggTheme.spacing.xl),
                )
            }

            else -> items(orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOpenOrder(order.id) },
                    modifier = Modifier.padding(horizontal = horizontal).animateItem(),
                )
            }
        }
    }
}

@Composable
private fun Header(
    today: String,
    isSearching: Boolean,
    onToggleSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = UggTheme.colors

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "Мастерская".uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)
            Text(text = "Заказы", style = UggTheme.typography.displayL.copy(fontWeight = FontWeight.SemiBold), color = colors.content)
            Text(text = today, style = UggTheme.typography.bodySmall, color = colors.contentSecondary)
        }
        UggIconButton(
            icon = if (isSearching) UggIcons.Close else UggIcons.Search,
            contentDescription = if (isSearching) "Закрыть поиск" else "Поиск по заказам",
            onClick = onToggleSearch,
        )
    }
}

@Composable
private fun SearchField(query: String, onQuery: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = UggTheme.colors
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated, UggTheme.shapes.pill)
            .border(1.dp, colors.outline, UggTheme.shapes.pill)
            .padding(horizontal = UggTheme.spacing.m, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = UggIcons.Search, contentDescription = null, tint = colors.contentSecondary, modifier = Modifier.size(18.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(text = "Номер, имя или телефон", style = UggTheme.typography.body, color = colors.contentSecondary)
            }
            BasicTextField(
                value = query,
                onValueChange = onQuery,
                singleLine = true,
                textStyle = UggTheme.typography.body.copy(color = colors.content),
                cursorBrush = SolidColor(colors.content),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            )
        }
    }
}

@Composable
private fun Summary(state: OrdersState, onFilter: (OrderFilter) -> Unit, modifier: Modifier = Modifier) {
    val colors = UggTheme.colors

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SummaryTile(
            count = state.newCount,
            label = state.newCount.plural("новый", "новых", "новых"),
            color = colors.attention,
            onClick = { onFilter(OrderFilter.New) },
            modifier = Modifier.weight(1f),
        )
        SummaryTile(
            count = state.inWorkCount,
            label = "в работе",
            color = colors.accentStrong,
            onClick = { onFilter(OrderFilter.InWork) },
            modifier = Modifier.weight(1f),
        )
        SummaryTile(
            count = state.readyCount,
            label = "к выдаче",
            color = colors.info,
            onClick = { onFilter(OrderFilter.Ready) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryTile(count: Int, label: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(UggTheme.shapes.medium)
            .background(UggTheme.colors.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = count.toString(),
            style = UggTheme.typography.displayL.copy(fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 34.sp),
            color = color,
        )
        Text(text = label, style = UggTheme.typography.caption, color = UggTheme.colors.contentSecondary)
    }
}

@Composable
private fun Filters(state: OrdersState, onFilter: (OrderFilter) -> Unit, horizontal: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = horizontal),
        horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.xs),
    ) {
        OrderFilter.entries.forEach { filter ->
            val count = when (filter) {
                OrderFilter.New -> state.newCount
                OrderFilter.InWork -> state.inWorkCount
                OrderFilter.Ready -> state.readyCount
                OrderFilter.All -> null
            }
            FilterChip(
                text = if (count == null) filter.title else "${filter.title} · $count",
                isSelected = filter == state.filter,
                onClick = { onFilter(filter) },
            )
        }
    }
}

@Composable
private fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.pill

    Text(
        text = text,
        style = UggTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = if (isSelected) colors.contentInverse else colors.content,
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) colors.surfaceInverse else Color.Transparent)
            .border(1.dp, if (isSelected) colors.surfaceInverse else colors.outline, shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    )
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.medium

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.outline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(UggTheme.spacing.m),
        verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.xs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "№ ${order.number}",
                style = UggTheme.typography.displayM,
                color = colors.content,
                modifier = Modifier.weight(1f),
            )
            StatusBadge(order.status)
        }
        Text(
            text = "${order.customer.name} · ${order.customer.phone}",
            style = UggTheme.typography.label.copy(fontWeight = FontWeight.Medium),
            color = colors.content,
        )
        Text(text = order.itemsSummary, style = UggTheme.typography.bodySmall, color = colors.contentSecondary)
        HorizontalDivider(thickness = 1.dp, color = colors.outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = order.delivery.short, style = UggTheme.typography.bodySmall, color = colors.content)
                Text(text = order.timeline, style = UggTheme.typography.caption, color = colors.contentSecondary)
            }
            Text(text = order.total, style = UggTheme.typography.heading.copy(fontWeight = FontWeight.Bold), color = colors.content)
        }
    }
}

@Composable
internal fun StatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (container, content) = status.badgeColors()

    Text(
        text = status.badgeLabel,
        style = UggTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
        color = content,
        modifier = modifier
            .background(container, UggTheme.shapes.pill)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

/** The second line under the delivery: when it was ordered, or when it will be ready. */
private val Order.timeline: String
    get() {
        val readyDay = readyAt?.toDayAndMonth()
        return when {
            status == OrderStatus.Ready -> if (readyDay != null) "готова с $readyDay" else "готова, ждёт клиента"
            status.isActive && status != OrderStatus.Accepted && readyDay != null -> "будет готова к $readyDay"
            else -> createdAt
        }
    }

private val OrderFilter.title: String
    get() = when (this) {
        OrderFilter.New -> "Новые"
        OrderFilter.InWork -> "В работе"
        OrderFilter.Ready -> "Готовы"
        OrderFilter.All -> "Все"
    }

private val OrderFilter.emptyText: String
    get() = when (this) {
        OrderFilter.New -> "Новых заказов нет"
        OrderFilter.InWork -> "Сейчас ничего не шьётся"
        OrderFilter.Ready -> "Нечего выдавать"
        OrderFilter.All -> "Заказов пока нет"
    }
