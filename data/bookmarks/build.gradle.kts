import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.kotlin.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.preferences)
            implementation(projects.data.linkdingApi)
            implementation(projects.data.database)
            implementation(libs.ktor.client.core)
            api(projects.data.models)
            api(libs.kotlinResult)
            api(libs.paging.common)
        }
    }
}

addKspDependencyForAllTargets(libs.kimchi.compiler)
addKspDependencyForAllTargets(libs.anvil.compiler)
