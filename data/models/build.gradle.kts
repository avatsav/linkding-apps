plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.datetime)
      implementation(libs.kotlin.serialization.json)
    }
  }
}
