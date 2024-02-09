plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.data.models)
            implementation(projects.data.bookmarks)
            implementation(projects.data.configuration)
            implementation(libs.paging.common)

            implementation(libs.kotlin.atomicfu)
            implementation(libs.paging.common)
            implementation(libs.kotlinResult)
            implementation(libs.kotlinResultCoroutines)
        }
    }
}
