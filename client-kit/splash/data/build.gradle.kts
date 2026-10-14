plugins {
    alias(libs.plugins.setup.multiplatform.feature)
}

kotlin {
    android {
        namespace = "com.ugg.admin.kit.splash.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.clientKit.splashDomain)
            implementation(projects.clientKit.coreDomain)
            implementation(projects.clientKit.coreData)

            // api, not implementation: they appear in the repositories' public constructors,
            // so the presentation module must see them to resolve the DI binding.
            api(projects.infrastructure.apiAuth)
            api(projects.infrastructure.keystoreSession)
            // AccessService.clear() returns KeystoreWriteResult from the foundation module.
            implementation(projects.infrastructure.keystoreFoundation)
            api(projects.infrastructure.firebaseAuth)
            api(projects.infrastructure.firebaseFirestore)
            implementation(projects.infrastructure.apiCore)
        }

        commonTest.dependencies {
            implementation(projects.infrastructure.apiCore)
        }
    }
}
