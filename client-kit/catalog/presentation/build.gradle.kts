plugins {
    alias(libs.plugins.setup.multiplatform.feature.compose)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.catalog.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.catalogData)
            implementation(projects.clientKit.catalogDomain)

            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
            implementation(projects.clientKit.corePresentation)

            // TemplateCore: the scope the Firestore services live in.
            implementation(projects.infrastructure.apiCore)
        }
    }
}
