plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.ui.theme)
      implementation(libs.compose.components.resources)
    }
  }
}
