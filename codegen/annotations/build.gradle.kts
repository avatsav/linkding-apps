plugins { id("convention.kmp.lib") }

kotlin {
  android {
    namespace = "dev.avatsav.linkding.codegen.annotations"
    compileSdk { version = release(36) }
  }
}
