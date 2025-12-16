plugins { id("convention.kmp.lib") }

kotlin {
  android {
    namespace = "dev.avatsav.linkding.db"
    compileSdk { version = release(36) }
  }
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.data.models)
      api(libs.paging.common)
    }
  }
}
