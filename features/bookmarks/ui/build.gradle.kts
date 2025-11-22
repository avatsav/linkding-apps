plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.features.bookmarks.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.connectivity)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
      implementation(projects.ui.theme)
      implementation(libs.compose.foundation)
      implementation(libs.compose.m3expressive)
      implementation(libs.compose.viewmodel)
      implementation(libs.paging.compose)
      implementation(libs.kotlin.datetime)
    }
  }
}

android { namespace = "dev.avatsav.linkding.bookmarks.ui" }
