plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.coroutines.core)
      api(libs.molecule)
      api(libs.compose.lifecycle)
      api(libs.compose.viewmodel)
    }
  }
}
