plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.linkding.bind)
            implementation(projects.data.database)
            implementation(libs.ktor.client.core)
            api(projects.data.models)
            api(libs.kotlinResult)
            api(libs.paging.common)
        }
    }
}
