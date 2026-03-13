plugins { id("convention.kmp.lib") }

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.data.database)
      implementation(projects.data.models)
    }
  }
}
