plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines.core)
            api(projects.core.base)
            api(projects.core.logging)

            implementation(libs.kotlin.inject.runtime)
        }
        androidMain.dependencies {
            api(libs.androidx.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.kotest.assertions)
            implementation(libs.turbine)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.internet"
}
