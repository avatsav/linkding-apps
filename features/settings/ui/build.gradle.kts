plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.features.settings.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.preferences)
      implementation(projects.core.viewmodel)
      implementation(projects.ui.compose)
      implementation(projects.ui.navigation)
    }
  }
}
