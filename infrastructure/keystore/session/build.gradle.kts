plugins {
    alias(libs.plugins.setup.multiplatform.library)
}

kotlin {
    android {
        namespace = "com.template.client.infrastructure.keystore.session"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.infrastructure.keystoreFoundation)
        }
    }
}
