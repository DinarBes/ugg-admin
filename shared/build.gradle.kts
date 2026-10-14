import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.setup.multiplatform.feature.compose)
    id("co.touchlab.skie")
}

kotlin {
    android {
        namespace = "com.ugg.admin.shared"
    }

    val frameworkName = "UggAdminShared"
    val xcFramework = XCFramework(frameworkName)
    val modules = listOf(
        projects.infrastructure.apiCore,
        projects.infrastructure.apiAuth,
        projects.infrastructure.firebaseAuth,
        projects.infrastructure.firebaseFirestore,
        projects.infrastructure.keystoreFoundation,
        projects.infrastructure.keystoreSession,

        projects.clientKit.coreData,
        projects.clientKit.coreDomain,
        projects.clientKit.corePresentation,

        projects.clientKit.navigationData,
        projects.clientKit.navigationDomain,
        projects.clientKit.navigationPresentation,

        projects.clientKit.authData,
        projects.clientKit.authDomain,
        projects.clientKit.authPresentation,

        projects.clientKit.splashData,
        projects.clientKit.splashDomain,
        projects.clientKit.splashPresentation,

        projects.clientKit.ordersData,
        projects.clientKit.ordersDomain,
        projects.clientKit.ordersPresentation,

        projects.clientKit.catalogData,
        projects.clientKit.catalogDomain,
        projects.clientKit.catalogPresentation,

        projects.clientKit.storesData,
        projects.clientKit.storesDomain,
        projects.clientKit.storesPresentation,

        projects.clientKit.clientsData,
        projects.clientKit.clientsDomain,
        projects.clientKit.clientsPresentation,

        projects.clientKit.contentData,
        projects.clientKit.contentDomain,
        projects.clientKit.contentPresentation,

        projects.clientKit.settingsData,
        projects.clientKit.settingsDomain,
        projects.clientKit.settingsPresentation,
    )

    targets.filterIsInstance<KotlinNativeTarget>().forEach { target ->
        target.binaries.framework {
            baseName = frameworkName
            isStatic = false

            modules.forEach { export(it) }

            xcFramework.add(this)
        }
    }

    sourceSets.commonMain.dependencies {
        modules.forEach { api(it) }

        implementation(libs.kotlinx.datetime)
    }
}

skie {
    features {
        enableFlowCombineConvertorPreview = true
    }
}
