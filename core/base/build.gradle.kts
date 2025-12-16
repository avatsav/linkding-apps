plugins { id("convention.kmp.lib") }

kotlin {
  android {
    namespace = "dev.avatsav.linkding.core"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlin.atomicfu)
      api(libs.kotlin.coroutines.core)
      api(libs.kermit)
      api(libs.kotlinResult)
      api(libs.kotlinResultCoroutines)
      api(libs.paging.common)
      implementation(libs.uuid)
    }
  }
}
