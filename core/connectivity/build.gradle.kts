import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logging)
            api(libs.kotlin.inject.runtime)
            api(libs.kotlin.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.kotest.assertions)
            implementation(libs.turbine)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.internet"
}

addKspDependencyForAllTargets(libs.kimchi.compiler)
