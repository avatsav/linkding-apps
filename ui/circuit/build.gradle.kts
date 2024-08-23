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
            implementation(projects.core.logging)
            api(libs.circuit.foundation)
            api(libs.circuit.overlay)
            api(libs.circuitx.overlays)
            api(libs.paging.common)
            api(projects.thirdparty.paging.compose)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.circuit"
}
