plugins {
  `kotlin-dsl`
  alias(libs.plugins.ktfmt)
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.android.kmp.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.kotlin.serialization.gradlePlugin)
  compileOnly(libs.compose.gradlePlugin)
  compileOnly(libs.composeCompiler.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
  compileOnly(libs.ktfmt.gradlePlugin)
  compileOnly(libs.detekt.gradlePlugin)
  compileOnly(libs.metro.gradlePlugin)
  compileOnly(libs.gradleVersions.gradlePlugin)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmToolchain.get())) } }

ktfmt { googleStyle() }

gradlePlugin {
  plugins {
    register("kmpLibrary") {
      id = "convention.kmp.lib"
      implementationClass = "dev.avatsav.gradle.KmpLibraryPlugin"
    }

    register("composeMultiplatform") {
      id = "convention.compose"
      implementationClass = "dev.avatsav.gradle.ComposePlugin"
    }

    register("androidApplication") {
      id = "convention.android.app"
      implementationClass = "dev.avatsav.gradle.AndroidApplicationPlugin"
    }
  }
}
