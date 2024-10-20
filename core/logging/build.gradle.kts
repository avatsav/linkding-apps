import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(libs.kermit)
            api(libs.kotlin.inject.runtime)
        }
    }
}

addKspDependencyForAllTargets(libs.kimchi.compiler)
