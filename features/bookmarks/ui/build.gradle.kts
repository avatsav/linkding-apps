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
      api(projects.features.bookmarks.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.connectivity)
      implementation(projects.ui.circuit)
      implementation(projects.ui.compose)
      implementation(projects.ui.screens)
      implementation(projects.ui.theme)
      implementation(libs.circuit.retained)
      implementation(libs.circuit.overlay)
      implementation(libs.paging.compose)
      implementation(libs.kotlin.datetime)
    }
  }
}

android { namespace = "dev.avatsav.linkding.bookmarks.ui" }

ksp { arg("circuit.codegen.mode", "metro") }

addKspDependencyForAllTargets(libs.circuit.codegen)
