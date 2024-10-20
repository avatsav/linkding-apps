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
            api(libs.kotlin.inject.anvil.runtime)
            api(libs.kotlin.inject.anvil.runtime.optional)
        }
    }
}

addKspDependencyForAllTargets(libs.kotlin.inject.anvil.compiler)
