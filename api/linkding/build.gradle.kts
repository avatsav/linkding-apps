plugins {
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.base)
            api(projects.core.preferences)
            api(projects.core.logging)

            implementation(libs.kotlin.coroutines.core)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)

            implementation(project.dependencies.platform(libs.arrow.bom))
            implementation(libs.arrow.core)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
