import dev.avatsav.gradle.configureComposeAndroidPreviews

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
            api(compose.preview)
            api(compose.uiTooling)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.shared"
}

configureComposeAndroidPreviews()
