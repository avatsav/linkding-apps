plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.data.models)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.compose.m3expressive)
      implementation(libs.kermit)
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
