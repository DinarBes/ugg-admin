plugins {
    alias(libs.plugins.setup.multiplatform.feature)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.settings.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.settingsDomain)
            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
        }
    }
}
