import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines.core)
            api(libs.kotlin.inject.runtime)
            api(libs.kimchi.annotations)
        }
    }
}

addKspDependencyForAllTargets(libs.kimchi.compiler)
