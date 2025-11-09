plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.compose.foundation)
      implementation(libs.compose.lifecycle)
      implementation(libs.compose.viewmodel)
    }
  }
}

android { namespace = "dev.avatsav.linkding.di" }
