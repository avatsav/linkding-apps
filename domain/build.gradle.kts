import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.data.auth)
            implementation(projects.data.bookmarks)
            implementation(libs.kotlin.atomicfu)
            implementation(libs.kotlinResultCoroutines)
            implementation(libs.kimchi.annotations)
            api(projects.data.models)
            api(libs.kotlinResult)
            api(libs.paging.common)
        }
    }
}

addKspDependencyForAllTargets(libs.kimchi.compiler)
