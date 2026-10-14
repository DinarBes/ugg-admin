package com.ugg.admin.kit.clients.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.clients.domain.entities.Client
import com.ugg.admin.kit.clients.domain.states.ClientsState
import com.ugg.admin.kit.core.data.formatting.plural
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggSearchField
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

@Composable
internal fun ClientsView(
    state: ClientsState,
    contentPadding: PaddingValues,
    onQuery: (String) -> Unit,
    onRetry: () -> Unit,
    onOpenOrder: (String) -> Unit,
) {
    val colors = UggTheme.colors
    val horizontal = UggTheme.spacing.l
    val clients = state.visibleClients

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.background),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + UggTheme.spacing.xl,
            bottom = contentPadding.calculateBottomPadding() + UggTheme.spacing.l,
        ),
    ) {
        item(key = "header") {
            Header(state = state, modifier = Modifier.padding(horizontal = horizontal))
        }
        item(key = "search") {
            UggSearchField(
                query = state.query,
                onQueryChange = onQuery,
                placeholder = "Имя или телефон",
                modifier = Modifier.padding(horizontal = horizontal).padding(top = 14.dp, bottom = 14.dp),
            )
        }

        when {
            state.clients.isEmpty() && state.error != null -> item(key = "error") {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = horizontal, vertical = UggTheme.spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.error?.message ?: "Не удалось загрузить клиентов",
                        style = UggTheme.typography.body,
                        color = colors.contentSecondary,
                        textAlign = TextAlign.Center,
                    )
                    UggButton(text = "Повторить", onClick = onRetry, style = UggButtonStyle.Secondary, showArrow = false)
                }
            }

            state.clients.isEmpty() && state.isLoading -> item(key = "loading") {
                Box(Modifier.fillMaxWidth().padding(vertical = UggTheme.spacing.xl), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.accent)
                }
            }

            clients.isEmpty() -> item(key = "empty") {
                Text(
                    text = if (state.query.isBlank()) "Пока никто не зарегистрировался" else "Никого не нашлось",
                    style = UggTheme.typography.body,
                    color = colors.contentSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = horizontal, vertical = UggTheme.spacing.xl),
                )
            }

            // One white card around the list, as in the design. A workshop has dozens of customers,
            // not thousands, so the card is a single item.
            else -> item(key = "clients") {
                val shape = UggTheme.shapes.medium
                Column(
                    modifier = Modifier
                        .padding(horizontal = horizontal)
                        .fillMaxWidth()
                        .clip(shape)
                        .background(colors.surfaceElevated)
                        .border(1.dp, colors.outline, shape)
                        .padding(horizontal = UggTheme.spacing.m, vertical = 6.dp),
                ) {
                    clients.forEachIndexed { index, client ->
                        if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.outline)
                        ClientRow(
                            client = client,
                            onClick = (client.activeOrder?.orderId ?: client.lastOrderId)?.let { orderId -> { onOpenOrder(orderId) } },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(state: ClientsState, modifier: Modifier = Modifier) {
    val colors = UggTheme.colors
    val total = state.clients.size

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = "Покупатели".uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)
        Text(text = "Клиенты", style = UggTheme.typography.displayL.copy(fontWeight = FontWeight.SemiBold), color = colors.content)
        if (total > 0) {
            Text(
                text = "$total ${total.plural("человек", "человека", "человек")} · ${state.activeCount} с заказом в работе",
                style = UggTheme.typography.bodySmall,
                color = colors.contentSecondary,
            )
        }
    }
}

@Composable
private fun ClientRow(client: Client, onClick: (() -> Unit)?) {
    val colors = UggTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(vertical = UggTheme.spacing.s),
        horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(name = client.name)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = client.name ?: "Без имени",
                style = UggTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = colors.content,
            )
            Text(
                text = "${client.phone} · ${client.size ?: "размер не указан"}",
                style = UggTheme.typography.caption,
                color = colors.contentSecondary,
            )
            OrdersLine(client)
        }
        if (onClick != null) {
            Icon(imageVector = UggIcons.ChevronRight, contentDescription = null, tint = colors.contentSecondary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun OrdersLine(client: Client) {
    val colors = UggTheme.colors
    val active = client.activeOrder
    val count = client.ordersCount

    if (active != null) {
        val more = (count - 1).takeIf { it > 0 }?.let { " · ещё $it ${it.plural("заказ", "заказа", "заказов")}" }.orEmpty()
        Text(
            text = "№ ${active.number} · ${active.status}$more",
            style = UggTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = colors.accentStrong,
        )
    } else {
        Text(
            text = if (count > 0) {
                "$count ${count.plural("заказ", "заказа", "заказов")} · сейчас ничего в работе"
            } else {
                "без заказов · зарегистрирован ${client.registeredAt}"
            },
            style = UggTheme.typography.caption,
            color = colors.contentSecondary,
        )
    }
}

/** Initials on cream, or a person icon for someone without a name yet. */
@Composable
private fun Avatar(name: String?) {
    val colors = UggTheme.colors
    val initials = name
        ?.split(' ')
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?.takeIf { it.isNotEmpty() }

    Box(
        modifier = Modifier.size(44.dp).clip(UggTheme.shapes.pill).background(colors.surface),
        contentAlignment = Alignment.Center,
    ) {
        if (initials != null) {
            Text(text = initials, style = UggTheme.typography.label.copy(fontWeight = FontWeight.Bold), color = colors.accentStrong)
        } else {
            Icon(imageVector = UggIcons.User, contentDescription = null, tint = colors.accentStrong, modifier = Modifier.size(20.dp))
        }
    }
}
