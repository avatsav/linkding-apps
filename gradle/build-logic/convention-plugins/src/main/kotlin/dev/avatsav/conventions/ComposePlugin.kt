package dev.avatsav.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.compose")
        val composeCompilerVersion = findVersion("jetbrains-compose-compiler")
        with(extensions.getByType<ComposeExtension>()) {
            kotlinCompilerPlugin.set(composeCompilerVersion)
        }
    }
}
