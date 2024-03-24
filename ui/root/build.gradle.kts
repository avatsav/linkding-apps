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
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            api(projects.ui.core.theme)
            api(projects.ui.core.screens)
            api(libs.circuit.foundation)
            api(libs.circuit.retained)
            api(libs.circuit.overlay)
            api(libs.circuitx.gestureNavigation)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(compose.uiTooling)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
    buildFeatures.compose = true
}
configureComposeAndroidPreviews()
