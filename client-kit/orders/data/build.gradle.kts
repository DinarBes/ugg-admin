plugins {
    alias(libs.plugins.setup.multiplatform.feature)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.orders.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.ordersDomain)
            implementation(projects.clientKit.coreDomain)
        }
    }
}
