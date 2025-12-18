plugins {
  alias(libs.plugins.android.kmp.library)
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  androidLibrary {
    namespace = "dev.avatsav.linkding.viewmodel"
    compileSdk = 36
    minSdk = 30
  }

  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.coroutines.core)
      api(libs.molecule)
      api(libs.compose.lifecycle)
      api(libs.compose.viewmodel)
    }
  }
}
