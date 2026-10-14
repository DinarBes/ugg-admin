plugins {
    alias(libs.plugins.setup.multiplatform.feature.compose)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.content.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.contentData)
            implementation(projects.clientKit.contentDomain)

            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
            implementation(projects.clientKit.corePresentation)

            // TemplateCore: the scope the Firestore services live in.
            implementation(projects.infrastructure.apiCore)
        }
    }
}
