plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(compose.materialIconsExtended) // Expose icons for UI components
      api(projects.ui.theme) // Expose theme for UI components
    }
  }
}

android { namespace = "dev.avatsav.linkding.ui.compose" }
