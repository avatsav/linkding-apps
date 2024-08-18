plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.theme)
            implementation(projects.core.logging)
            api(libs.paging.common)
            api(projects.thirdparty.paging.compose)
            api(libs.circuit.foundation)
            api(libs.circuit.overlay)
            api(libs.circuitx.overlays)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.compose"
}
