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
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(projects.thirdparty.paging.compose)
      api(projects.ui.theme)
      api(projects.ui.screens)
    }
  }
}

android { namespace = "dev.avatsav.linkding.bookmarks.ui" }

ksp { arg("circuit.codegen.mode", "metro") }

addKspDependencyForAllTargets(libs.circuit.codegen)
