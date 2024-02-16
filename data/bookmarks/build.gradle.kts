plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.data.models)
            implementation(projects.linkding.bind)
            implementation(libs.kotlinResult)
            implementation(libs.paging.common)
        }
    }
}
