package com.ugg.admin.kit.navigation.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.components.UggTabBar
import com.ugg.admin.kit.core.presentation.external.components.UggTabItem
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.orders.presentation.external.view.OrdersScreen

internal enum class MainTab(val label: String, val icon: () -> ImageVector) {
    Orders("Заказы", { UggIcons.Orders }),
    Catalog("Каталог", { UggIcons.Boot }),
    Content("Контент", { UggIcons.Content }),
    Clients("Клиенты", { UggIcons.User }),
}

/**
 * The admin shell behind [com.ugg.admin.kit.splash.domain.entities.SplashFlow.Launch]: tab bar plus
 * the selected section. Screens pushed on the root back stack (an order card) cover it, tab bar
 * included. The selected tab is owned by the host so it survives those screens.
 */
@Composable
internal fun MainTabsView(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    onOpenOrder: (orderId: String) -> Unit,
) {
    val tabs = MainTab.entries

    Scaffold(
        containerColor = UggTheme.colors.background,
        bottomBar = {
            UggTabBar(
                items = tabs.map { UggTabItem(it.label, it.icon()) },
                selectedIndex = selected.ordinal,
                onSelect = { onSelect(tabs[it]) },
            )
        },
    ) { padding ->
        when (selected) {
            MainTab.Orders -> OrdersScreen(contentPadding = padding, onOpenOrder = onOpenOrder)
            MainTab.Catalog -> ComingSoon(kicker = "Модель «Классика»", title = "Каталог", contentPadding = padding)
            MainTab.Content -> ComingSoon(kicker = "Тексты приложения", title = "Контент", contentPadding = padding)
            MainTab.Clients -> ComingSoon(kicker = "Покупатели", title = "Клиенты", contentPadding = padding)
        }
    }
}

/** Sections that are designed but not built yet. */
@Composable
private fun ComingSoon(kicker: String, title: String, contentPadding: PaddingValues) {
    val colors = UggTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(contentPadding)
            .padding(horizontal = UggTheme.spacing.l, vertical = UggTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = kicker.uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)
        Text(text = title, style = UggTheme.typography.displayL.copy(fontWeight = FontWeight.SemiBold), color = colors.content)
        Text(text = "Раздел появится следующим шагом", style = UggTheme.typography.bodySmall, color = colors.contentSecondary)
    }
}
