@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("gradle/build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "linkding-apps"

include(
    ":app:shared",
    ":app:android",
    ":app:desktop",
    ":linkding:api",
    ":linkding:bind",
    ":core:base",
    ":core:logging",
    ":core:preferences",
    ":core:connectivity",
    ":data:models",
    ":data:bookmarks",
    ":data:database",
    ":domain",
    ":ui:theme",
    ":ui:screens",
    ":ui:compose",
    ":ui:root",
    ":features:bookmarks",
    ":features:add-bookmark",
    ":features:setup",
    ":features:settings",
    ":features:tags"
)
