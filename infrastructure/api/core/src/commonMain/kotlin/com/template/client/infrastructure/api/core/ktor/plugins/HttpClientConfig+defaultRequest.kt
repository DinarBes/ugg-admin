package com.template.client.infrastructure.api.core.ktor.plugins

import com.template.client.infrastructure.api.core.TemplateApi
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.path

internal fun HttpClientConfig<*>.templateDefaultRequest(api: TemplateApi) {
    defaultRequest {
        url {
            protocol = api.protocol
            host = api.host
            path(*api.path.toTypedArray())
        }
    }
}
