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
      implementation(projects.core.connectivity)
      implementation(projects.ui.circuit)
      implementation(projects.ui.compose)
      implementation(projects.ui.screens)
      implementation(projects.ui.theme)
      // Note: data.models comes through api dependency, no need for direct dep

      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(libs.circuit.retained)
      implementation(libs.circuit.overlay)
      implementation(libs.paging.compose)
    }
  }
}

android { namespace = "dev.avatsav.linkding.bookmarks.ui" }

ksp { arg("circuit.codegen.mode", "metro") }

addKspDependencyForAllTargets(libs.circuit.codegen)
