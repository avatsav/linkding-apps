plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    jvmTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(compose.desktop.currentOs)
      implementation(libs.compose.ui.test.junit4)
    }
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
      implementation(libs.compose.components.resources)
    }
  }
}
