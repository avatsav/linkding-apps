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
            implementation(projects.core.connectivity)
            implementation(projects.domain)
            implementation(projects.data.models)

            implementation(projects.ui.core.theme)
            implementation(projects.ui.core.screens)
            implementation(projects.ui.core.compose)
            implementation(projects.ui.tags)

            api(libs.circuit.foundation)
            api(libs.circuit.retained)
            api(libs.circuitx.overlays)
            api(libs.kotlinResult)
            api(libs.kotlinResultCoroutines)
            implementation(libs.paging.compose)

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
    namespace = "dev.avatsav.linkding.ui.setup"
}
configureComposeAndroidPreviews()
