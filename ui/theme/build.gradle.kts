plugins {
  alias(libs.plugins.android.kmp.library)
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    androidLibrary {
      namespace = "dev.avatsav.linkding.ui.theme"
      compileSdk = 36
      minSdk = 30
    }

    commonMain.dependencies {
      api(libs.compose.foundation)
      api(libs.compose.m3expressive)
      api(libs.compose.materialIconsExtended)
      implementation(libs.compose.components.resources)
    }
  }
}
