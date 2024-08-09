package dev.avatsav.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.configure<ComposeCompilerGradlePluginExtension> {
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html#includesourceinformation
            includeSourceInformation.set(true)

            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html#stabilityconfigurationfile
            stabilityConfigurationFile.set(rootProject.file("compose-stability.conf"))
        }
    }
}
