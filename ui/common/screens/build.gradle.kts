plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.circuit.runtime)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.common.screens"
}
