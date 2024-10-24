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

            implementation(libs.kimchi.circuit.annotations)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.ui.bookmarks"
}

addKspDependencyForAllTargets(libs.kimchi.circuit.compiler)
addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kimchi.compiler)
