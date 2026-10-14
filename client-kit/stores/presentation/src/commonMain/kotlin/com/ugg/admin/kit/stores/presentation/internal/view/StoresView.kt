@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.stores.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.data.formatting.formatPhone
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.entities.openingLabel
import com.ugg.admin.kit.stores.domain.entities.summary
import com.ugg.admin.kit.stores.domain.states.StoresState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun StoresView(
    state: StoresState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenStore: (String) -> Unit,
) {
    val colors = UggTheme.colors
    val now = remember { Clock.System.now() }

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        BackHeader(kicker = "Контент", title = "Точки", onBack = onBack)

        when {
            state.stores.isNotEmpty() -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = UggTheme.spacing.l,
                    end = UggTheme.spacing.l,
                    top = UggTheme.spacing.xs,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + UggTheme.spacing.l,
                ),
                verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
            ) {
                item(key = "hint") {
                    Text(
                        text = "Адреса, телефоны и графики. Клиенты видят их во вкладке «Точки».",
                        style = UggTheme.typography.bodySmall,
                        color = colors.contentSecondary,
                    )
                }
                items(state.stores, key = { it.id }) { store ->
                    StoreCard(store = store, status = store.openingLabel(now), onClick = { onOpenStore(store.id) })
                }
            }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.error?.message ?: "Не удалось загрузить точки",
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
private fun StoreCard(store: Store, status: String, onClick: () -> Unit) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.medium
    val isOpen = status.startsWith("Открыто")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.outline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(UggTheme.spacing.m),
        horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = store.city, style = UggTheme.typography.displayM, color = colors.content, modifier = Modifier.weight(1f))
                Text(
                    text = status,
                    style = UggTheme.typography.caption,
                    color = if (isOpen) colors.info else colors.contentSecondary,
                    modifier = Modifier
                        .clip(UggTheme.shapes.pill)
                        .background(if (isOpen) colors.infoContainer else colors.surface)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Text(text = store.name, style = UggTheme.typography.label, color = colors.content)
            Text(text = "${store.address} · ${store.phone.formatPhone()}", style = UggTheme.typography.bodySmall, color = colors.contentSecondary)
            Text(text = store.schedule.summary(), style = UggTheme.typography.bodySmall, color = colors.content)
        }
        Icon(imageVector = UggIcons.ChevronRight, contentDescription = null, tint = colors.contentSecondary, modifier = Modifier.size(18.dp))
    }
}
