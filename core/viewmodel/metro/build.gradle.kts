plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.coroutines.core)
      api(libs.kermit)
      implementation(libs.compose.viewmodel)
    }
  }
}

android {
  namespace = "dev.avatsav.linkding.viewmodel"
}
