package dev.avatsav.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal const val JETBRAINS_COMPOSE_PLUGIN = "org.jetbrains.compose"
internal const val COMPOSE_COMPILER_PLUGIN = "org.jetbrains.kotlin.plugin.compose"

class ComposePlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply(JETBRAINS_COMPOSE_PLUGIN)
      pluginManager.apply(COMPOSE_COMPILER_PLUGIN)

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
