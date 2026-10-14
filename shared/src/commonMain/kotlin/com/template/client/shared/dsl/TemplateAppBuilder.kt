package com.template.client.shared.dsl

import com.template.client.infrastructure.api.core.TemplateApi
import com.template.client.infrastructure.api.core.TemplateCore
import com.template.client.infrastructure.keystore.foundation.external.Keystore
import com.template.client.infrastructure.keystore.foundation.external.KeystoreConfiguration
import com.template.client.infrastructure.keystore.foundation.external.TemplateKeystore
import com.template.client.kit.core.domain.AppEnvironment
import io.ktor.client.plugins.logging.LogLevel

class TemplateAppBuilder {
    var core = TemplateCore {
        api = TemplateApi.Client.Localhost
        logLevel = LogLevel.INFO
    }

    var environment = AppEnvironment.PRODUCTION

    /**
     * The secure store backing the session tokens.
     *
     * The default platform keystore works as-is on Apple. On Android it needs a `Context`, so
     * supply one through the [keystore] block (`keystore { context = … }`); until then reads and
     * writes report `KeystoreFailure.NotConfigured` rather than crashing. Tests and
     * `AppEnvironment.MOCK` assign an `InMemoryKeystore()` directly.
     */
    var keystore: Keystore = TemplateKeystore(KeystoreConfiguration())

    /** Builds the default [TemplateKeystore] from a configured [KeystoreConfiguration]. */
    fun keystore(block: KeystoreConfiguration.() -> Unit) {
        keystore = TemplateKeystore(KeystoreConfiguration().apply(block))
    }
}
