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
            implementation(projects.ui.common.compose)
            implementation(projects.data.models)

            api(projects.ui.common.screens)
            api(libs.circuit.foundation)

            implementation(libs.circuit.retained)
            api(libs.kotlinResult)
            api(libs.kotlinResultCoroutines)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
}
