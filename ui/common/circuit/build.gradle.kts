import dev.avatsav.gradle.configureComposeAndroidPreviews

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.common.shared)
            api(compose.ui)
            api(compose.material3)
            api(libs.circuit.foundation)
            api(libs.circuit.overlay)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.circuit"
}

configureComposeAndroidPreviews()
