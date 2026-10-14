package com.ugg.admin.kit.navigation.presentation.internal.view

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.ugg.admin.kit.catalog.presentation.external.view.CatalogScreen
import com.ugg.admin.kit.clients.presentation.external.view.ClientsScreen
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.presentation.external.view.ContentScreen
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
 * the selected section. Screens pushed on the root back stack (an order card, a content editor, the stores) cover it, tab bar
 * included. The selected tab is owned by the host so it survives those screens.
 */
@Composable
internal fun MainTabsView(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    onOpenOrder: (orderId: String) -> Unit,
    onOpenStores: () -> Unit,
    onOpenContent: (ContentSection) -> Unit,
    onOpenSettings: () -> Unit,
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
            MainTab.Orders -> OrdersScreen(contentPadding = padding, onOpenOrder = onOpenOrder, onOpenSettings = onOpenSettings)
            MainTab.Catalog -> CatalogScreen(contentPadding = padding)
            MainTab.Content -> ContentScreen(contentPadding = padding, onOpenSection = onOpenContent, onOpenStores = onOpenStores)
            MainTab.Clients -> ClientsScreen(contentPadding = padding, onOpenOrder = onOpenOrder)
        }
    }
}
