plugins {
    alias(libs.plugins.setup.multiplatform.feature.compose)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.navigation.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.splashDomain)
            implementation(projects.clientKit.splashPresentation)

            implementation(projects.clientKit.navigationData)
            implementation(projects.clientKit.navigationDomain)

            implementation(projects.clientKit.authDomain)
            implementation(projects.clientKit.authPresentation)

            implementation(projects.clientKit.catalogPresentation)
            implementation(projects.clientKit.clientsPresentation)
            implementation(projects.clientKit.contentDomain)
            implementation(projects.clientKit.contentPresentation)

            implementation(projects.clientKit.storesDomain)
            implementation(projects.clientKit.storesPresentation)

            implementation(projects.clientKit.ordersDomain)
            implementation(projects.clientKit.ordersPresentation)

            implementation(projects.clientKit.corePresentation)

            implementation(libs.navigation3.ui)
        }
    }
}
