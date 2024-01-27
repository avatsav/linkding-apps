plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.preferences)
            implementation(projects.data.models)
            implementation(projects.data.bookmarks)

            implementation(libs.kotlin.atomicfu)
            implementation(libs.paging.common)
            implementation(libs.kotlinResult)
            implementation(libs.kotlinResultCoroutines)
        }
    }
}
