plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.settings.ui"
    compileSdk { version = release(36) }
  }

  sourceSets {
    commonMain.dependencies {
      api(projects.features.settings.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.preferences)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
      implementation(libs.compose.viewmodel.navigation3)
      implementation(libs.metro.viewmodel)
      implementation(libs.metro.viewmodel.compose)
    }
  }
}
