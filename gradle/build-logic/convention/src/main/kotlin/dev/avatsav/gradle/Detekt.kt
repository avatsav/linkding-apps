package dev.avatsav.gradle

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

internal fun Project.configureDetekt() {
  // Skip Detekt for thirdparty module
  if (path.startsWith(":thirdparty")) return

  pluginManager.apply("io.gitlab.arturbosch.detekt")
  configure<DetektExtension> {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    parallel = true
  }

  val detektComposeRules = libs.findLibrary("detektComposeRules").get()
  dependencies { add("detektPlugins", detektComposeRules) }

  tasks.withType<Detekt>().configureEach {
    exclude { element -> element.file.path.contains("/build/generated/") }
    jvmTarget = findVersion("detektJvmTarget")
    parallel = true
  }

  tasks.register("detektAll") {
    group = "Verification"
    description = "Run all detekt tasks with type resolution for Kotlin Multiplatform"
    // Excluding iOS targets for now
    // Metro iOS issue: https://github.com/ZacSweers/metro/issues/460
    val detektTasks =
      tasks.withType<Detekt>().filterNot { it.name.contains("ios", ignoreCase = true) }
    dependsOn(detektTasks)
  }
}
