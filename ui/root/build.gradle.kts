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
            implementation(projects.data.models)

            implementation(projects.ui.common.theme)
            implementation(projects.ui.common.screens)
            implementation(libs.circuit.foundation)
            implementation(libs.circuit.retained)
            implementation(libs.circuit.overlay)
            implementation(libs.circuitx.gestureNavigation)

            implementation(compose.foundation)
            implementation(compose.material3)
            api(libs.kotlinResult)
            api(libs.kotlinResultCoroutines)
        }
        androidMain.dependencies {
            api(compose.preview)
            api(compose.uiTooling)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
    buildFeatures.compose = true
}
configureComposeAndroidPreviews()
