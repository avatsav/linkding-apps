plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.data.models)
            api(libs.multiplatform.settings)
            api(libs.multiplatform.settings.coroutines)
            api(libs.kotlin.inject.runtime)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.androidx.preference)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.prefs"
}
