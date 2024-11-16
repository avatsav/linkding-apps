plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.theme)
            implementation(projects.ui.compose)
            api(libs.circuit.foundation)
            api(libs.circuit.retained)
            api(libs.circuit.codegen.annotations)
            api(libs.circuit.overlay)
            api(libs.circuitx.overlays)
            api(libs.paging.common)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.circuit"
}
