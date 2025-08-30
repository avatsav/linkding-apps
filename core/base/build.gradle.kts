plugins {
  id("convention.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.atomicfu)
      api(libs.kotlin.coroutines.core)
      api(libs.kotlin.inject.runtime)
      api(libs.anvil.runtime)
      api(libs.anvil.runtime.optional)
      api(libs.kermit)
      api(libs.kotlinResult)
      api(libs.kotlinResultCoroutines)
      implementation(libs.uuid)
    }
  }
}
