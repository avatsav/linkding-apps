plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.data.models)
            implementation(libs.kotlinResult)
            implementation(libs.kotlin.inject.runtime)
        }
        jvmMain.dependencies {
            implementation(libs.unfurl)
        }
    }
}
