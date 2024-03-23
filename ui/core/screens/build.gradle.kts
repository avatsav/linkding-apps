plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.models)
            api(libs.circuit.runtime)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.screens"
}
