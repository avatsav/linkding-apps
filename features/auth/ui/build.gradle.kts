plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.auth.api)
      implementation(projects.ui.theme)
      implementation(projects.ui.navigation)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(libs.compose.foundation)
      implementation(libs.compose.m3expressive)
      implementation(libs.compose.viewmodel)
      implementation(libs.compose.viewmodel.navigation3)
      implementation(libs.metro.viewmodel)
      implementation(libs.metro.viewmodel.compose)
    }
  }
}

android { namespace = "dev.avatsav.linkding.auth.ui" }
