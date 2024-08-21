plugins {
    id("convention.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            implementation(projects.core.preferences)
            implementation(projects.data.linkdingApi)
            implementation(libs.ktor.client.core)
            api(projects.data.models)
            api(libs.kotlinResult)
        }
    }
}
