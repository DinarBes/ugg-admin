plugins {
    alias(libs.plugins.setup.multiplatform.feature.compose)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.orders.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.ordersData)
            implementation(projects.clientKit.ordersDomain)

            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
            implementation(projects.clientKit.corePresentation)

            // TemplateCore: the scope the Firestore services live in.
            implementation(projects.infrastructure.apiCore)
        }
    }
}
