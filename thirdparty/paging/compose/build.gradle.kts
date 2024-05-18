import dev.avatsav.gradle.configureParcelize

/**
 * Moving androidx-paging-compose and adding support for other platforms
 * https://android.googlesource.com/platform/frameworks/support/+/HEAD/paging/paging-compose/
 *
 * TODO: Remove this module when androidx.paging.compose adds support for other platforms.
 */

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    configureParcelize()
    sourceSets {
        commonMain.dependencies {
            api(projects.core.parcelize)
            api(libs.paging.common)
            api(compose.runtime)
        }
    }
}

android {
    namespace = "androidx.paging.compose"
}
