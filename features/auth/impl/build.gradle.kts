plugins {
  id("convention.kmp.lib")
  alias(libs.plugins.ksp)
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.auth.impl"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.auth.api)
      implementation(projects.core.base)
      implementation(projects.core.di)
      implementation(projects.core.preferences)
      implementation(projects.data.linkdingApi)
    }
  }
}
