plugins {
    alias(libs.plugins.setup.multiplatform.feature.compose)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.settings.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.settingsData)
            implementation(projects.clientKit.settingsDomain)

            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
            implementation(projects.clientKit.corePresentation)

            // TemplateCore: the scope the Firebase services live in.
            implementation(projects.infrastructure.apiCore)
        }
    }
}
