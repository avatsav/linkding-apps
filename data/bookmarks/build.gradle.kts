plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.data.models)
            implementation(projects.api.linkding)
            implementation(libs.kotlinResult)
        }
    }
}
