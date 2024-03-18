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
            implementation(projects.core.preferences)
            implementation(projects.domain)
            api(projects.data.models)

            api(projects.ui.core.theme)
            api(projects.ui.core.compose)
            api(projects.ui.core.screens)
            api(libs.circuit.foundation)
            api(libs.circuit.retained)
            api(libs.circuit.overlay)
            api(libs.circuitx.overlays)
            api(libs.kotlinResult)
            api(libs.kotlinResultCoroutines)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
        }
        androidMain.dependencies {
            api(compose.preview)
            api(compose.uiTooling)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.settings"
}

configureComposeAndroidPreviews()
