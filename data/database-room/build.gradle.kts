plugins {
  id("convention.kmp.lib")
  alias(libs.plugins.ksp)
}

ksp { arg("room.schemaLocation", layout.projectDirectory.dir("schemas").asFile.absolutePath) }

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

    jvmTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlin.coroutines.test)
      implementation(libs.kotest.assertions)
      implementation(libs.turbine)
    }
  }
}
