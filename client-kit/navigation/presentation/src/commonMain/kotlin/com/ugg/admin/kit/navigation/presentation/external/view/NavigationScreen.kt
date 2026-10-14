package com.ugg.admin.kit.navigation.presentation.external.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.ugg.admin.kit.auth.domain.entities.AuthFlow
import com.ugg.admin.kit.auth.presentation.external.view.InitiateScreen
import com.ugg.admin.kit.auth.presentation.external.view.OtpScreen
import com.ugg.admin.kit.content.domain.entities.ContentFlow
import com.ugg.admin.kit.content.presentation.external.view.ContentEditorScreen
import com.ugg.admin.kit.navigation.domain.external.contexts.NavigationContext
import com.ugg.admin.kit.navigation.domain.external.entities.NavigationEvent
import com.ugg.admin.kit.navigation.presentation.internal.view.MainTab
import com.ugg.admin.kit.navigation.presentation.internal.view.MainTabsView
import com.ugg.admin.kit.orders.domain.entities.OrdersFlow
import com.ugg.admin.kit.orders.presentation.external.view.OrderDetailScreen
import com.ugg.admin.kit.settings.domain.entities.SettingsFlow
import com.ugg.admin.kit.settings.presentation.external.view.SettingsScreen
import com.ugg.admin.kit.splash.domain.entities.SplashDestination
import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import com.ugg.admin.kit.splash.presentation.external.view.NoAccessScreen
import com.ugg.admin.kit.splash.presentation.external.view.SplashScreen
import com.ugg.admin.kit.splash.presentation.external.view.models.SplashViewModel
import com.ugg.admin.kit.stores.domain.entities.StoresFlow
import com.ugg.admin.kit.stores.presentation.external.view.StoreScreen
import com.ugg.admin.kit.stores.presentation.external.view.StoresScreen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.kodein.di.compose.localDI
import org.kodein.di.compose.viewmodel.rememberViewModel
import org.kodein.di.instance

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun NavigationScreen() {
    val di = localDI()
    val navigationContext: NavigationContext by di.instance()

    // Held by the host, not by the splash entry: it decides the first destination and keeps
    // listening for authorization events long after the splash leaves the back stack.
    val splashViewModel: SplashViewModel by rememberViewModel()
    val splashState by splashViewModel.state.collectAsState()

    val config = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(SplashDestination.Splash::class, SplashDestination.Splash.serializer())
                subclass(SplashFlow.Auth::class, SplashFlow.Auth.serializer())
                subclass(SplashFlow.Launch::class, SplashFlow.Launch.serializer())
                subclass(SplashFlow.NoAccess::class, SplashFlow.NoAccess.serializer())
                subclass(AuthFlow.Otp::class, AuthFlow.Otp.serializer())
                subclass(OrdersFlow.Detail::class, OrdersFlow.Detail.serializer())
                subclass(StoresFlow.Stores::class, StoresFlow.Stores.serializer())
                subclass(ContentFlow.Editor::class, ContentFlow.Editor.serializer())
                subclass(SettingsFlow.Settings::class, SettingsFlow.Settings.serializer())
                subclass(StoresFlow.Store::class, StoresFlow.Store.serializer())
            }
        }
    }

    val backStack = rememberNavBackStack(config, SplashDestination.Splash)
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Orders) }

    LaunchedEffect(Unit) {
        navigationContext.events.collect { event ->
            when (event) {
                is NavigationEvent.OnNavigate -> {
                    with(event) { backStack.block() }
                    backStack.add(event.route)
                }
                is NavigationEvent.OnBack -> backStack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<SplashDestination.Splash> {
                SplashScreen(error = splashState.error, onRetry = { splashViewModel.fetch() })
            }
            entry<SplashFlow.Auth> {
                InitiateScreen()
            }
            entry<SplashFlow.Launch> {
                MainTabsView(
                    selected = selectedTab,
                    onSelect = { selectedTab = it },
                    onOpenOrder = { navigationContext.tryNavigate(OrdersFlow.Detail(it)) },
                    onOpenStores = { navigationContext.tryNavigate(StoresFlow.Stores) },
                    onOpenContent = { navigationContext.tryNavigate(ContentFlow.Editor(it)) },
                    onOpenSettings = { navigationContext.tryNavigate(SettingsFlow.Settings) },
                )
            }
            entry<SettingsFlow.Settings> {
                // The splash owns the session: after signing out it clears the stack and shows
                // the phone step; the orders tab is where the next admin starts.
                SettingsScreen(
                    onBack = { navigationContext.back() },
                    onSignOut = {
                        selectedTab = MainTab.Orders
                        splashViewModel.signOut()
                    },
                )
            }
            entry<ContentFlow.Editor> {
                ContentEditorScreen(section = it.section, onBack = { navigationContext.back() })
            }
            entry<StoresFlow.Stores> {
                StoresScreen(
                    onBack = { navigationContext.back() },
                    onOpenStore = { navigationContext.tryNavigate(StoresFlow.Store(it)) },
                )
            }
            entry<StoresFlow.Store> {
                StoreScreen(storeId = it.storeId, onBack = { navigationContext.back() })
            }
            entry<OrdersFlow.Detail> {
                // TODO: «+ Добавить» фото — once the admin app uploads to Firebase Storage.
                OrderDetailScreen(orderId = it.orderId, onBack = { navigationContext.back() })
            }
            entry<AuthFlow.Otp> {
                OtpScreen(phone = it.phone, sessionId = it.sessionId, onBack = { navigationContext.back() })
            }
            entry<SplashFlow.NoAccess> {
                NoAccessScreen(
                    phone = it.phone,
                    isOffline = it.isOffline,
                    isChecking = splashState.isChecking,
                    onRecheck = { splashViewModel.recheck() },
                    onSignOut = { splashViewModel.signOut() },
                )
            }
        }
    )
}
