rootProject.name = "ugg-admin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

data class Module(val name: String, val path: String)

includeBuild("build-logic")

include(":shared")
include(":androidApp")

listOf(
    Module(":infrastructure:api-core", "infrastructure/api/core"),
    Module(":infrastructure:api-auth", "infrastructure/api/auth"),
    Module(":infrastructure:firebase-auth", "infrastructure/firebase/auth"),
    Module(":infrastructure:firebase-firestore", "infrastructure/firebase/firestore"),
    Module(":infrastructure:keystore-foundation", "infrastructure/keystore/foundation"),
    Module(":infrastructure:keystore-session", "infrastructure/keystore/session"),

    Module(":client-kit:core-data", "client-kit/core/data"),
    Module(":client-kit:core-domain", "client-kit/core/domain"),
    Module(":client-kit:core-presentation", "client-kit/core/presentation"),

    Module(":client-kit:navigation-data", "client-kit/navigation/data"),
    Module(":client-kit:navigation-domain", "client-kit/navigation/domain"),
    Module(":client-kit:navigation-presentation", "client-kit/navigation/presentation"),

    Module(":client-kit:auth-data", "client-kit/auth/data"),
    Module(":client-kit:auth-domain", "client-kit/auth/domain"),
    Module(":client-kit:auth-presentation", "client-kit/auth/presentation"),

    Module(":client-kit:splash-data", "client-kit/splash/data"),
    Module(":client-kit:splash-domain", "client-kit/splash/domain"),
    Module(":client-kit:splash-presentation", "client-kit/splash/presentation"),

    Module(":client-kit:orders-data", "client-kit/orders/data"),
    Module(":client-kit:orders-domain", "client-kit/orders/domain"),
    Module(":client-kit:orders-presentation", "client-kit/orders/presentation"),

    Module(":client-kit:catalog-data", "client-kit/catalog/data"),
    Module(":client-kit:catalog-domain", "client-kit/catalog/domain"),
    Module(":client-kit:catalog-presentation", "client-kit/catalog/presentation"),

    Module(":client-kit:stores-data", "client-kit/stores/data"),
    Module(":client-kit:stores-domain", "client-kit/stores/domain"),
    Module(":client-kit:stores-presentation", "client-kit/stores/presentation"),

    Module(":client-kit:clients-data", "client-kit/clients/data"),
    Module(":client-kit:clients-domain", "client-kit/clients/domain"),
    Module(":client-kit:clients-presentation", "client-kit/clients/presentation"),

    Module(":client-kit:content-data", "client-kit/content/data"),
    Module(":client-kit:content-domain", "client-kit/content/domain"),
    Module(":client-kit:content-presentation", "client-kit/content/presentation"),

    Module(":client-kit:settings-data", "client-kit/settings/data"),
    Module(":client-kit:settings-domain", "client-kit/settings/domain"),
    Module(":client-kit:settings-presentation", "client-kit/settings/presentation"),
).forEach {
    include(it.name)
    project(it.name).projectDir = file(it.path)
}
