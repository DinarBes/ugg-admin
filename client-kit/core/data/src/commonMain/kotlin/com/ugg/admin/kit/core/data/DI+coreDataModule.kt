package com.ugg.admin.kit.core.data

import com.ugg.admin.infrastructure.api.auth.services.AuthService
import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import com.ugg.admin.infrastructure.firebase.auth.createPhoneAuthService
import com.ugg.admin.infrastructure.firebase.firestore.createCatalogService
import com.ugg.admin.infrastructure.firebase.firestore.createContentService
import com.ugg.admin.infrastructure.firebase.firestore.createOrderService
import com.ugg.admin.infrastructure.firebase.firestore.createUserService
import com.ugg.admin.infrastructure.firebase.firestore.services.CatalogService
import com.ugg.admin.infrastructure.firebase.firestore.services.ContentService
import com.ugg.admin.infrastructure.firebase.firestore.services.OrderService
import com.ugg.admin.infrastructure.firebase.firestore.services.UserService
import com.ugg.admin.kit.core.data.scopes.TemplateCoreScope
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.scoped
import org.kodein.di.singleton

val coreDataModule get() = DI.Module("core-data") {
    bind<AuthService>() with scoped(TemplateCoreScope).singleton { AuthService(instance()) }

    // One instance for the whole app: the splash and the sign-in screens watch the same user.
    bind<PhoneAuthService>() with scoped(TemplateCoreScope).singleton { createPhoneAuthService() }

    // Firestore, the same project as the client app: orders, product and stores, customers, screen texts.
    bind<OrderService>() with scoped(TemplateCoreScope).singleton { createOrderService() }
    bind<CatalogService>() with scoped(TemplateCoreScope).singleton { createCatalogService() }
    bind<UserService>() with scoped(TemplateCoreScope).singleton { createUserService() }
    bind<ContentService>() with scoped(TemplateCoreScope).singleton { createContentService() }
}
