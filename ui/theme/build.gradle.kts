plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.compose.foundation)
      api(libs.compose.m3expressive)
      api(libs.compose.materialIconsExtended)
      implementation(libs.compose.components.resources)
    }
  }
}
