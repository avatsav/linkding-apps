plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.di"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.compose.viewmodel) // For ViewModel base class
      implementation(libs.metro.viewmodel)
    }
  }
}
