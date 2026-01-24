@file:Suppress("UnstableApiUsage")

pluginManagement {
  // Non-delegate APIs are annoyingly not public so we have to use withGroovyBuilder
  // Reference https://github.com/slackhq/circuit/blob/main/settings.gradle.kts
  fun hasProperty(key: String): Boolean {
    return settings.withGroovyBuilder { "hasProperty"(key) as Boolean }
  }

  includeBuild("gradle/build-logic")
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()

    if (hasProperty("linkding.enableMavenSnapshots")) {
      maven("https://central.sonatype.com/repository/maven-snapshots")
    }

    if (hasProperty("linkding.enableMavenLocal")) {
      mavenLocal()
    }

    if (hasProperty("linkding.enableKotlinDevBuilds")) {
      maven("https://redirector.kotlinlang.org/maven/bootstrap") {
        name = "kotlin-bootstrap"
        content { includeGroupByRegex("org\\.jetbrains\\.kotlin.*") }
      }
      maven("https://redirector.kotlinlang.org/maven/dev/") {
        name = "kotlin-dev"
        content { includeGroupByRegex("org\\.jetbrains\\.kotlin.*") }
      }
    }
  }
}

dependencyResolutionManagement {
  // Non-delegate APIs are annoyingly not public so we have to use withGroovyBuilder
  // Reference: https://github.com/slackhq/circuit/blob/main/settings.gradle.kts
  fun hasProperty(key: String): Boolean {
    return settings.withGroovyBuilder { "hasProperty"(key) as Boolean }
  }

  repositories {
    google()
    mavenCentral()

    if (hasProperty("linkding.enableMavenSnapshots")) {
      maven("https://central.sonatype.com/repository/maven-snapshots")
    }

    if (hasProperty("linkding.enableMavenLocal")) {
      mavenLocal()
    }

    if (hasProperty("linkding.enableKotlinDevBuilds")) {
      maven("https://redirector.kotlinlang.org/maven/bootstrap") {
        name = "kotlin-bootstrap"
        content { includeGroupByRegex("org\\.jetbrains\\.kotlin.*") }
      }
      maven("https://redirector.kotlinlang.org/maven/dev/") {
        name = "kotlin-dev"
        content { includeGroupByRegex("org\\.jetbrains\\.kotlin.*") }
      }
    }
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "linkding-apps"

include(
  ":app:shared",
  ":app:android",
  ":app:desktop",
  ":core:base",
  ":core:preferences",
  ":core:connectivity",
  ":core:di",
  ":core:viewmodel",
  ":data:models",
  ":data:database",
  ":data:database-sqldelight",
  ":data:linkding-api",
  ":ui:theme",
  ":ui:compose",
  ":ui:navigation",
  ":features:auth:ui",
  ":features:auth:api",
  ":features:auth:impl",
  ":features:bookmarks:ui",
  ":features:bookmarks:api",
  ":features:bookmarks:impl",
  ":features:settings:ui",
  ":features:settings:api",
  ":features:settings:impl",
  ":codegen:annotations",
  ":codegen:compiler",
)
