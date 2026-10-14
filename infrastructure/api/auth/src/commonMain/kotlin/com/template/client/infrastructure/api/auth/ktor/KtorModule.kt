package com.template.client.infrastructure.api.auth.ktor

import com.template.client.infrastructure.api.auth.dsl.TemplateAuthConfiguration
import com.template.client.infrastructure.api.auth.ktor.plugins.templateBearerAuth
import io.ktor.client.HttpClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindings.overriddenInstanceOf
import org.kodein.di.instance
import org.kodein.di.singleton

internal val ktorModule: DI.Module
    get() = DI.Module("ktor-auth-module", allowSilentOverride = true) {
        bind<HttpClient>() with singleton {
            val client: HttpClient = overriddenInstanceOf()
            val configuration: TemplateAuthConfiguration = instance()

            client.config {
                templateBearerAuth(configuration.bearerAuthConfigBlock)
            }
        }
    }
