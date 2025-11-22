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
      implementation(projects.core.di)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(libs.compose.foundation)
      implementation(libs.compose.m3expressive)
      implementation(projects.features.auth.api)
      api(projects.ui.theme)
      api(projects.ui.navigation)
    }
  }
}

android { namespace = "dev.avatsav.linkding.auth.ui" }
