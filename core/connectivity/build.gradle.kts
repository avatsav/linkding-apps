plugins {
  alias(libs.plugins.android.kmp.library)
  id("convention.kmp.lib")
}

kotlin {
  androidLibrary {
    namespace = "dev.avatsav.linkding.internet"
    compileSdk = 36
    minSdk = 30
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
