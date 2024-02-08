plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)
            api(libs.paging.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.common.shared"
}
