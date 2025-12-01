plugins {
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
      implementation(libs.compose.viewmodel.navigation3)
      implementation(libs.metro.viewmodel)
    }
  }
}
