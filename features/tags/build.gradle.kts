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
            implementation(projects.ui.compose)
            implementation(projects.ui.circuit)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.circuit.retained)
            implementation(libs.circuit.overlay)
            api(projects.ui.theme)
            api(projects.ui.screens)
            api(libs.circuit.foundation)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.tags"
}
