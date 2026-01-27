plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kermit)
      api(libs.compose.navigation3)
      api(libs.compose.navigationEvent)
    }
  }
}
