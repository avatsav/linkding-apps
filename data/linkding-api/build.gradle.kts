plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.kotlin.serialization.json)
      implementation(libs.kotlin.datetime)
      implementation(libs.ktor.client.contentNegotiation)
      implementation(libs.ktor.serialization.json)
      api(libs.ktor.client.core)
      api(libs.ktor.client.logging)
      api(libs.kotlinResult)
    }
  }
}
