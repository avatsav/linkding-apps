plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines.core)
            api(projects.core.base)
            api(projects.core.preferences)

            implementation(libs.kotlin.inject.runtime)
            implementation(libs.kermit)
        }
    }
}
