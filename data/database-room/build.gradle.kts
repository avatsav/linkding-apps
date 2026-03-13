plugins {
  id("convention.kmp.lib")
  alias(libs.plugins.ksp)
}

dependencies {
  add("kspAndroid", libs.room3.compiler)
  add("kspIosArm64", libs.room3.compiler)
  add("kspIosSimulatorArm64", libs.room3.compiler)
  add("kspJvm", libs.room3.compiler)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.data.database)
      implementation(projects.data.models)
      implementation(libs.room3.runtime)
      implementation(libs.room3.paging)
      implementation(libs.sqlite.bundled)
    }
  }
}
