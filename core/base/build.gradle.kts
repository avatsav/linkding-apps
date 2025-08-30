plugins {
  id("convention.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.atomicfu)
      api(libs.kotlin.coroutines.core)
      api(libs.kermit)
      api(libs.kotlinResult)
      api(libs.kotlinResultCoroutines)
      implementation(libs.uuid)
    }
  }
}
