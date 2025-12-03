plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.features.settings.api)
      implementation(projects.features.settings.impl)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.preferences)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.theme)
      implementation(projects.ui.navigation)
      implementation(projects.ui.compose)
      implementation(projects.ui.theme)
      implementation(libs.compose.foundation)
      implementation(libs.compose.m3expressive)
      implementation(libs.compose.viewmodel)
      implementation(libs.compose.viewmodel.navigation3)
      implementation(libs.metro.viewmodel)
      implementation(libs.metro.viewmodel.compose)
    }
  }
}

android { namespace = "dev.avatsav.linkding.settings.ui" }
