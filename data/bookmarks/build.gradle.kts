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
            implementation(projects.data.database)
            implementation(libs.ktor.client.core)
            api(projects.data.models)
            api(libs.kotlinResult)
            api(libs.paging.common)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
