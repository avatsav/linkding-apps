package dev.avatsav.gradle

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

class AndroidApplicationPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      with(pluginManager) { apply("com.android.application") }
      configureAndroidApplication()
      configureJavaToolchain()
      configureKtfmt()
      configureDetekt()
      configureKotlinCompilerOptions()
    }
}

private fun Project.configureAndroidApplication() {
  extensions.configure<ApplicationExtension> {
    val targetSdkVersion = findVersion("targetSdk").toInt()
    compileSdk = findVersion("compileSdk").toInt()
    defaultConfig {
      minSdk = findVersion("minSdk").toInt()
      targetSdk = targetSdkVersion
    }
  }
}

private fun Project.configureKotlinCompilerOptions() {
  extensions.configure<KotlinAndroidExtension> {
    jvmToolchain(findVersion("jvmToolchain").toInt())
    compilerOptions {
      freeCompilerArgs.addAll(CompilerOptions.freeCompilerArgs)
      optIn.addAll(CompilerOptions.optIn)
    }
  }
}

internal fun Project.configureJavaToolchain() {
  extensions.configure<JavaPluginExtension> {
    toolchain { languageVersion.set(JavaLanguageVersion.of(findVersion("jvmToolchain"))) }
  }
}
