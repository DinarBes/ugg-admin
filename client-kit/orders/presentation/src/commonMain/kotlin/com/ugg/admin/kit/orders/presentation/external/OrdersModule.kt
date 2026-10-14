package com.ugg.admin.kit.orders.presentation.external

import com.ugg.admin.kit.orders.data.useCases.MockOrdersUseCase
import com.ugg.admin.kit.orders.domain.useCases.OrdersUseCase
import com.ugg.admin.kit.orders.presentation.external.view.models.OrderDetailViewModel
import com.ugg.admin.kit.orders.presentation.external.view.models.OrdersViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

// One store for the whole app, so a stage changed on the order card shows up in the list. Kept out
// of Kodein on purpose: a binding of the Mock type would also match OrdersUseCase.
private val mockOrders by lazy { MockOrdersUseCase() }

val ordersModule: DI.Module
    get() = DI.Module("orders.module") {
        bind<OrdersViewModel>() with factory {
            OrdersViewModel(instance())
        }

        bind<OrderDetailViewModel>() with factory {
            OrderDetailViewModel(instance())
        }

        bind<OrdersUseCase>() with provider {
            // TODO: SharedOrdersUseCase on Firestore (`orders`, joined with `users` for the customer)
            //  once the admin app gets infrastructure/firebase; then switch on AppEnvironment here.
            mockOrders
        }
    }
