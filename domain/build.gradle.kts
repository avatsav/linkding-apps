plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.data.database)
            implementation(projects.data.bookmarks)
            implementation(libs.kotlin.atomicfu)
            implementation(libs.kotlinResultCoroutines)
            api(projects.data.models)
            api(libs.kotlinResult)
            api(libs.paging.common)
        }
    }
}
