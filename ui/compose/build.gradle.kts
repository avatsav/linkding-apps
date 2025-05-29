plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(compose.materialIconsExtended)
      implementation(projects.ui.theme)
    }
  }
}

android { namespace = "dev.avatsav.linkding.ui.compose" }
