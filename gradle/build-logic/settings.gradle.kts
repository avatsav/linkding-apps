@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://redirector.kotlinlang.org/maven/bootstrap") {
      name = "kotlin-bootstrap"
      content { includeGroupByRegex("org\\.jetbrains\\.kotlin.*") }
    }
    maven("https://redirector.kotlinlang.org/maven/dev/") {
      name = "kotlin-dev"
      content { includeGroupByRegex("org\\.jetbrains\\.kotlin.*") }
    }
  }
  versionCatalogs { create("libs") { from(files("../libs.versions.toml")) } }
}

rootProject.name = "build-logic"

include(":convention")
