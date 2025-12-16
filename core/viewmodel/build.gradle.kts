plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.viewmodel"
    compileSdk { version = release(36) }
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
