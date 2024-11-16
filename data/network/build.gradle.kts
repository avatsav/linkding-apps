import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.avatsav.linkding.data.network"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            api(libs.ktor.client.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

addKspDependencyForAllTargets(libs.anvil.compiler)
