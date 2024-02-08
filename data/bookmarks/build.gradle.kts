plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.data.models)
            implementation(projects.linkdingApi)
            implementation(libs.kotlinResult)
            implementation(libs.paging.common)
        }
    }
}
