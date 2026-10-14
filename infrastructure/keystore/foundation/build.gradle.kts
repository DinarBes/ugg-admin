plugins {
    // NOT setup-multiplatform-infrastructure: that plugin adds
    // api(libs.multiplatform.foundation.keystore) — the very library this module replaces.
    alias(libs.plugins.setup.multiplatform.library)
}

kotlin {
    android {
        namespace = "com.ugg.admin.infrastructure.keystore.foundation"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.security.crypto)
        }
    }
}
