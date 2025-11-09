plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.coroutines.core)
      api(libs.molecule)
      api(libs.compose.lifecycle)
      api(libs.compose.viewmodel)
    }
  }
}

android { namespace = "dev.avatsav.linkding.viewmodel" }
