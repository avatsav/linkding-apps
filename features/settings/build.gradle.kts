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
            implementation(projects.ui.compose)
            implementation(projects.ui.circuit)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.circuit.retained)
            implementation(libs.circuit.overlay)
            api(projects.ui.theme)
            api(projects.ui.screens)
        }
    }
}

android {
    namespace = "dev.avatsav.linkding.settings.ui"
}

ksp {
    arg("circuit.codegen.mode", "kotlin_inject_anvil")
    arg("kotlin-inject-anvil-contributing-annotations", "com.slack.circuit.codegen.annotations.CircuitInject")
}

addKspDependencyForAllTargets(libs.kimchi.circuit.compiler)
addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kimchi.compiler)
addKspDependencyForAllTargets(libs.anvil.compiler)
addKspDependencyForAllTargets(libs.circuit.codegen)
