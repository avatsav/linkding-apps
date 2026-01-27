plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.features.bookmarks.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.connectivity)
      implementation(projects.core.presenter)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
      implementation(libs.paging.compose)
      implementation(libs.kotlin.datetime)
    }
  }
}
