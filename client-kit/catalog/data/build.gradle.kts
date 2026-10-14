plugins {
    alias(libs.plugins.setup.multiplatform.feature)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.catalog.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.catalogDomain)
            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
