@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("gradle/build-logic")
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots")
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots")
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
  ":core:parcelize",
  ":core:viewmodel:metro",
  ":core:viewmodel:molecule",

  ":data:models",
  ":data:database",
  ":data:database-sqldelight",
  ":data:linkding-api",

  ":ui:theme",
  ":ui:screens",
  ":ui:compose",
  ":ui:circuit",

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
