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
      implementation(projects.ui.circuit)
      implementation(projects.ui.compose)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.components.resources)
      implementation(projects.features.auth.api)
      implementation(projects.domain)
      api(projects.ui.theme)
      api(projects.ui.screens)
      implementation(libs.kotlin.inject.runtime)
    }
  }
}

android { namespace = "dev.avatsav.linkding.auth.ui" }

ksp { arg("circuit.codegen.mode", "metro") }
addKspDependencyForAllTargets(libs.circuit.codegen)
