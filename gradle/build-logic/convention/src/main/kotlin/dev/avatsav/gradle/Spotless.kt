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
        val ktLintVersion = findVersion("ktlint")
        val ktLintCompose = findVersion("ktlintCompose")
        kotlin {
            target("src/**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            ktlint(ktLintVersion)
                .editorConfigOverride(
                    mapOf("android" to "true"),
                )
//                .customRuleSets(
//                    listOf("io.nlopez.compose.rules:ktlint:$ktLintCompose"),
//                )
        }
        kotlinGradle {
            target("*.kts")
            targetExclude("${layout.buildDirectory}/**/*.kts")
            ktlint(ktLintVersion)
        }
    }
}
