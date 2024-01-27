plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.data.models)

            api(libs.multiplatform.settings)
            api(libs.multiplatform.settings.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.kotlin.inject.runtime)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.prefs"
}
