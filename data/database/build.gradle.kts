plugins { id("convention.kmp.lib") }

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.data.models)
      api(libs.paging.common)
    }
  }
}
