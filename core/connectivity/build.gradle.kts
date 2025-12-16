plugins {
  id("convention.kmp.lib")
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.internet"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(projects.core.di)
    }
    androidMain.dependencies { implementation(libs.androidx.core) }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlin.coroutines.test)
      implementation(libs.kotest.assertions)
      implementation(libs.turbine)
    }
  }
}
