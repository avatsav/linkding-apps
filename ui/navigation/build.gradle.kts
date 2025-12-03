plugins {
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.ui.compose)
      implementation(libs.kotlin.serialization.json)
      implementation(projects.core.di)
      api(libs.compose.navigation3)
      api(libs.compose.navigationEvent)
    }
  }
}
