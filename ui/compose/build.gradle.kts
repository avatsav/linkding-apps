plugins {
  id("convention.kmp.lib")
  id("convention.compose")
}

kotlin {
  android {
    namespace = "dev.avatsav.linkding.ui.compose"
    compileSdk { version = release(36) }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.ui.theme)
    }
  }
}
