plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.core)
    }
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.ui.theme)
      implementation(libs.compose.components.resources)
    }
  }
}
