plugins {
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.data.models)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.compose.m3expressive)
      api(libs.compose.navigation3)
      api(libs.compose.navigationEvent)
    }
  }
}
