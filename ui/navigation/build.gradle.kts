plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.shared"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.data.models)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.compose.m3expressive)
      api(libs.compose.navigation3)
      api(libs.compose.navigationEvent)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlin.coroutines.test)
      implementation(libs.kotest.assertions)
      implementation(libs.turbine)
    }
  }
}
