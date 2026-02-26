plugins {
  id("convention.kmp.lib")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.datetime)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlin.serialization.json)
    }
  }
}
