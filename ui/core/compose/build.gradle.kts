plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.core.theme)
            implementation(projects.core.logging)
            api(libs.paging.compose)
            api(libs.circuit.foundation)
            api(libs.circuit.overlay)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.compose"
}
