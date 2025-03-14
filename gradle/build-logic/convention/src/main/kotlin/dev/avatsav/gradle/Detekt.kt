package dev.avatsav.gradle

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

internal fun Project.configureDetekt() {
  // Skip Spotless for thirdparty module
  if (path.startsWith(":thirdparty")) return

  pluginManager.apply("io.gitlab.arturbosch.detekt")
  configure<DetektExtension> {
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    parallel = true
  }
  tasks.withType<Detekt>().configureEach {
    exclude { element -> element.file.path.contains("/build/generated/") }
  }

  tasks.register("detektAll") {
    group = "Verification"
    description = "Run all detekt tasks with type resolution for Kotlin Multiplatform"
    dependsOn(tasks.withType<Detekt>())
  }

  val detektComposeRules = libs.findLibrary("detektComposeRules").get().get().toString()
  dependencies { add("detektPlugins", detektComposeRules) }
}
