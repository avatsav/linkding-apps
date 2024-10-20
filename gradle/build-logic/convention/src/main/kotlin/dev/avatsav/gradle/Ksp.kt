package dev.avatsav.gradle

import org.gradle.api.Project
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.Locale

fun Project.addKspDependencyForAllTargets(dependencyNotation: Any) {
    val kmpExtension = extensions.getByType<KotlinMultiplatformExtension>()
    dependencies {
        kmpExtension.targets
            .asSequence()
            .filter { target ->
                // Don't add KSP for common target, only final platforms
                target.platformType != KotlinPlatformType.common
            }
            .forEach { target ->
                add(
                    "ksp${target.targetName.capitalized()}",
                    dependencyNotation,
                )
            }
    }
}

fun String.capitalized(): CharSequence = let<CharSequence, CharSequence> {
    if (it.isEmpty()) {
        it
    } else {
        it[0].titlecase(
            Locale.getDefault(),
        ) + it.substring(1)
    }
}
