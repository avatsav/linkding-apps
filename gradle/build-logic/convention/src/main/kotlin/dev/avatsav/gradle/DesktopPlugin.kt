package dev.avatsav.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class DesktopApplicationPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.kotlin.jvm")

        apply("org.jetbrains.kotlin.plugin.serialization")
        apply("dev.zacsweers.metro")
      }
      configureJavaToolchain()
    }
}
