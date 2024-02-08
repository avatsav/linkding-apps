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
    ":androidApp",
    ":shared",
    ":core:base",
    ":core:logging",
    ":core:preferences",
    ":linkding",
    ":data:models",
    ":data:bookmarks",
    ":domain",
    ":ui:common:compose",
    ":ui:common:screens",
    ":ui:common:shared",
    ":ui:bookmarks",
    ":ui:setup",
    ":ui:root",
    ":unfurl"
)
