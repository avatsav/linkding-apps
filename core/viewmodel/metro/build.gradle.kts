plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.kermit)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.compose.viewmodel)
    }
  }
}

android { namespace = "dev.avatsav.linkding.viewmodel" }
