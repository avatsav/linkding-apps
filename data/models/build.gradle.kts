plugins {
  id("convention.kmp.lib")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.shared"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.datetime)
      implementation(libs.kotlin.serialization.json)
    }
  }
}
