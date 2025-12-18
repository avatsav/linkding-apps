plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.compose.viewmodel) // For ViewModel base class
      implementation(libs.metro.viewmodel)
    }
  }
}
