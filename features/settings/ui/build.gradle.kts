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
      api(projects.features.settings.api)
      implementation(projects.features.settings.impl)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.preferences)
      implementation(projects.ui.compose)
      implementation(projects.ui.circuit)
      implementation(projects.ui.theme)
      implementation(libs.circuit.retained)
      implementation(libs.circuit.overlay)
      api(projects.ui.theme)
      api(projects.ui.screens)
    }
  }
}

android { namespace = "dev.avatsav.linkding.settings.ui" }

ksp { arg("circuit.codegen.mode", "metro") }

addKspDependencyForAllTargets(libs.circuit.codegen)
