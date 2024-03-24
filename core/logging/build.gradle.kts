plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(libs.kermit)
            api(libs.kotlin.inject.runtime)
        }
    }
}
