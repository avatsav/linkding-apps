package dev.avatsav.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposePlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("org.jetbrains.compose")
      pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

      extensions.configure<ComposeCompilerGradlePluginExtension> {
        includeSourceInformation.set(true)
        stabilityConfigurationFiles.addAll(
          rootProject.layout.projectDirectory.file("compose-stability.conf")
        )
        val reportsDir = layout.buildDirectory.dir("compose-reports")
        reportsDestination.set(reportsDir)
        metricsDestination.set(reportsDir)
      }
    }
}
