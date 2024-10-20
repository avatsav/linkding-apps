package dev.avatsav.gradle

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * https://github.com/chrisbanes/tivi/blob/cbf022598832f881109fc8526d4f6b0e3c93a347/gradle/build-logic/convention/src/main/kotlin/app/tivi/gradle/KotlinMultiplatformConventionPlugin.kt#L83-L105
 */
fun Project.addKspDependencyForAllTargets(dependencyNotation: Any) =
    addKspDependencyForAllTargets("", dependencyNotation)

private fun Project.addKspDependencyForAllTargets(
    configurationNameSuffix: String,
    dependencyNotation: Any,
) {
    val kmpExtension = extensions.getByType<KotlinMultiplatformExtension>()
    dependencies {
        kmpExtension.targets
            .asSequence()
            .forEach { target ->
                val targetConfigSuffix =
                    if (target.targetName.equals("Metadata", true)) {
                        "CommonMainMetadata"
                    } else {
                        target.targetName.capitalized()
                    }
                val configName = "ksp$targetConfigSuffix"
                println("$name:$configName -> ${target.targetName}")
                add(configName, dependencyNotation)
            }
    }
}

fun Project.configureKspForCircuitCodegen() {
    extensions.configure<KspExtension> {
        arg("circuit.codegen.mode", "kotlin_inject_anvil")
        arg(
            "kotlin-inject-anvil-contributing-annotations",
            "com.slack.circuit.codegen.annotations.CircuitInject",
        )
    }
}
