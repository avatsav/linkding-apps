import dev.avatsav.gradle.addKspDependencyForAllTargets
import dev.avatsav.gradle.configureKspForCircuitCodegen

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
            implementation(projects.core.logging)
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

            implementation(libs.circuit.codegen.annotations)
//            implementation(libs.kotlin.inject.runtime)
            implementation(libs.kotlin.inject.anvil.runtime)
            implementation(libs.kotlin.inject.anvil.runtime.optional)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.setup"
}

configureKspForCircuitCodegen()
addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kotlin.inject.anvil.compiler)
addKspDependencyForAllTargets(libs.circuit.codegen)
