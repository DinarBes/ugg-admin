plugins {
    alias(libs.plugins.setup.multiplatform.feature)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.core.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.coreDomain)

            implementation(projects.infrastructure.apiCore)
            implementation(projects.infrastructure.apiAuth)

            implementation(libs.kotlinx.datetime)
        }
    }
}
