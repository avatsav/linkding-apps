plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.common.theme)
            implementation(projects.ui.common.screens)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)

        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
}
