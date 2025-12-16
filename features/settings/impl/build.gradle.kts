plugins { id("convention.kmp.lib") }

kotlin {
  android {
    namespace = "dev.avatsav.linkding.settings.impl"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      api(projects.features.settings.api)
      implementation(projects.core.preferences)
      implementation(projects.core.base)
      implementation(projects.core.di)
    }
  }
}
