plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    android {
      namespace = "dev.avatsav.linkding.ui.theme"
      compileSdk { version = release(36) }
    }

    commonMain.dependencies {
      api(libs.compose.foundation)
      api(libs.compose.m3expressive)
      api(libs.compose.materialIconsExtended)
      implementation(libs.compose.components.resources)
    }
  }
}
