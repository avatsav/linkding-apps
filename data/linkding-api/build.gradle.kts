plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
}

android { namespace = "dev.avatsav.linkding.api" }

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
    androidMain.dependencies { implementation(libs.ktor.client.okhttp) }
    jvmMain.dependencies { implementation(libs.ktor.client.okhttp) }
    iosMain.dependencies { implementation(libs.ktor.client.darwin) }
  }
}
