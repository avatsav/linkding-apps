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
            implementation(projects.ui.core.compose)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.circuit.retained)
            implementation(libs.circuit.overlay)
            implementation(libs.circuitx.overlays)
            api(projects.ui.core.theme)
            api(projects.ui.core.screens)
            api(libs.circuit.foundation)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.settings"
}
