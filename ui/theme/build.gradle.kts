plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.compose.foundation)
      api(libs.compose.m3expressive)
      api(libs.compose.materialIconsExtended)
    }
  }
}

android { namespace = "dev.avatsav.linkding.ui.theme" }
