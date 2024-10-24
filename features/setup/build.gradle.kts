import dev.avatsav.gradle.addKspDependencyForAllTargets

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.preferences)
            implementation(projects.domain)
            implementation(projects.ui.circuit)
            implementation(projects.ui.compose)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.circuit.retained)
            implementation(libs.kotlin.inject.runtime)
            api(projects.ui.theme)
            api(projects.ui.screens)
            api(libs.circuit.foundation)

            implementation(libs.kimchi.circuit.annotations)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kimchi.compiler)
addKspDependencyForAllTargets(libs.kimchi.circuit.compiler)
