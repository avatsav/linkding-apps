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
            implementation(projects.core.connectivity)
            implementation(projects.domain)
            implementation(projects.ui.compose)
            implementation(projects.ui.circuit)
            implementation(projects.features.tags)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.circuitx.overlays)
            implementation(libs.circuit.retained)
            implementation(projects.thirdparty.paging.compose)
            api(projects.ui.theme)
            api(projects.ui.screens)
            api(libs.circuit.foundation)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.bookmarks"
}

configureKspForCircuitCodegen()
addKspDependencyForAllTargets(libs.circuit.codegen)
addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kotlin.inject.anvil.compiler)
