plugins {
  alias(libs.plugins.android.kmp.library)
  id("convention.kmp.lib")
}

kotlin {
  androidLibrary {
    namespace = "dev.avatsav.linkding.api"
    compileSdk = 36
    minSdk = 30
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.kotlin.datetime)
      implementation(libs.ktor.client.contentNegotiation)
      implementation(libs.ktor.serialization.json)
      api(libs.ktor.client.core) // HTTP client types in public API
      api(libs.ktor.client.logging) // Logging types in public API
      api(libs.kotlinResult) // Result types exposed in public API
    }
    androidMain.dependencies { implementation(libs.ktor.client.okhttp) }
    jvmMain.dependencies { implementation(libs.ktor.client.okhttp) }
    iosMain.dependencies { implementation(libs.ktor.client.darwin) }
  }
}
