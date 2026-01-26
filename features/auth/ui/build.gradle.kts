plugins {
  id("convention.kmp.lib")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.auth.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
    }
  }
}
