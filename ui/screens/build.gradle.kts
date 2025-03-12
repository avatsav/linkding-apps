import dev.avatsav.gradle.configureParcelize

plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
  alias(libs.plugins.kotlin.parcelize)
}

kotlin {
  configureParcelize()
  sourceSets {
    commonMain.dependencies {
      api(projects.core.parcelize)
      api(libs.circuit.runtime)
    }
  }
}

android { namespace = "dev.avatsav.linkding.ui.screens" }
