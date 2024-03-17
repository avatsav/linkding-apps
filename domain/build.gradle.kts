plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.data.models)
            api(projects.data.bookmarks)
            api(projects.data.database)

            implementation(libs.paging.common)
            implementation(libs.kotlin.atomicfu)
            implementation(libs.paging.common)
            implementation(libs.kotlinResult)
            implementation(libs.kotlinResultCoroutines)
        }
    }
}
