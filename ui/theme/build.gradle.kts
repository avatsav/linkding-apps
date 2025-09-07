plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
  alias(libs.plugins.kotlin.parcelize)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(compose.foundation)
      api(libs.compose.m3expressive)
    }
  }
}

android { namespace = "dev.avatsav.linkding.ui.theme" }
