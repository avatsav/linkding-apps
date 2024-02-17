import dev.avatsav.gradle.configureComposeAndroidPreviews

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.domain)
            api(projects.data.models)

            api(projects.ui.common.theme)
            api(projects.ui.common.shared)
            api(projects.ui.common.screens)
            api(libs.circuit.foundation)
            api(libs.circuit.retained)
            api(libs.kotlinResult)
            api(libs.kotlinResultCoroutines)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
        }
        androidMain.dependencies {
            api(compose.preview)
            api(compose.uiTooling)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
}

configureComposeAndroidPreviews()
