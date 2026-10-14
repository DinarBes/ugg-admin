plugins {
    alias(libs.plugins.setup.multiplatform.feature)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.content.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.contentDomain)
            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)
        }
    }
}
