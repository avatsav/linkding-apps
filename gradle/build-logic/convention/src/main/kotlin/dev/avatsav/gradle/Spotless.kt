package dev.avatsav.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureSpotless() {
    // Skip Spotless for thirdparty module
    if (path.startsWith(":thirdparty")) return

    pluginManager.apply("com.diffplug.spotless")
    configure<SpotlessExtension> {
        lineEndings = LineEnding.PLATFORM_NATIVE
        val ktlintVersion = libs.findLibrary("ktlint").get().get().version
        val ktlintCompose = libs.findLibrary("ktlintCompose").get().get().toString()

        kotlin {
            target("src/**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            ktlint(ktlintVersion)
                .editorConfigOverride(
                    mapOf("android" to "true"),
                )
                .customRuleSets(
                    listOf(ktlintCompose),
                )
        }
        kotlinGradle {
            target("*.kts")
            targetExclude("${layout.buildDirectory}/**/*.kts")
            ktlint(ktlintVersion)
        }
    }
}
