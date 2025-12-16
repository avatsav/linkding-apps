plugins {
  id("convention.kmp.lib")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.auth.ui"
    compileSdk { version = release(36) }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.auth.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
      implementation(libs.compose.viewmodel.navigation3)
      implementation(libs.metro.viewmodel)
      implementation(libs.metro.viewmodel.compose)
    }
  }
}
