plugins {
    alias(libs.plugins.setup.multiplatform.infrastructure)
}

kotlin {
    android {
        namespace = "com.ugg.admin.infrastructure.api.auth"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.infrastructure.apiCore)
            api(libs.ktor.client.auth)
        }
    }
}
