plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kermit)
      api(libs.kotlin.coroutines.core)
      api(libs.molecule)
      api(libs.compose.lifecycle)
      api(libs.compose.foundation)
    }
  }
}
