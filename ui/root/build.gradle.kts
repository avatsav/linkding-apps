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
            implementation(projects.data.auth)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            api(projects.ui.theme)
            api(projects.ui.screens)
            api(libs.circuit.foundation)
            api(libs.circuit.retained)
            api(libs.circuit.overlay)
            api(libs.circuitx.gestureNavigation)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.root"
    buildFeatures.compose = true
}
