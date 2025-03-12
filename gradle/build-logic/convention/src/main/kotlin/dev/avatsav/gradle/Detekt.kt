package dev.avatsav.gradle

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureDetekt() {
    pluginManager.apply("io.gitlab.arturbosch.detekt")
    configure<DetektExtension> {
        config.setFrom("$rootDir/config/detekt/detekt.yml")
    }

    val detektComposeRules = libs.findLibrary("detektComposeRules").get().get().toString()
    dependencies {
        add("detektPlugins", detektComposeRules)
    }

    tasks.named("detekt") {
        // Skip detekt for thirdparty module
        if (path.startsWith(":thirdparty")) {
            enabled = false
        }
    }
}
