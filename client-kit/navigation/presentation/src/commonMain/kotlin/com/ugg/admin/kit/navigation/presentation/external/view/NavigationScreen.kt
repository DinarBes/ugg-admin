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
import com.ugg.admin.kit.navigation.domain.external.contexts.NavigationContext
import com.ugg.admin.kit.navigation.domain.external.entities.NavigationEvent
import com.ugg.admin.kit.navigation.presentation.internal.view.MainTab
import com.ugg.admin.kit.navigation.presentation.internal.view.MainTabsView
import com.ugg.admin.kit.orders.domain.entities.OrdersFlow
import com.ugg.admin.kit.orders.presentation.external.view.OrderDetailScreen
import com.ugg.admin.kit.splash.domain.entities.SplashDestination
import com.ugg.admin.kit.splash.domain.entities.SplashFlow
import com.ugg.admin.kit.splash.presentation.external.view.SplashScreen
import com.ugg.admin.kit.splash.presentation.external.view.models.SplashViewModel
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
                subclass(AuthFlow.Otp::class, AuthFlow.Otp.serializer())
                subclass(OrdersFlow.Detail::class, OrdersFlow.Detail.serializer())
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
                )
            }
            entry<OrdersFlow.Detail> {
                // TODO: «+ Добавить» фото — once the admin app uploads to Firebase Storage.
                OrderDetailScreen(orderId = it.orderId, onBack = { navigationContext.back() })
            }
            entry<AuthFlow.Otp> {
                OtpScreen(it.phone, it.sessionId)
            }
        }
    )
}
