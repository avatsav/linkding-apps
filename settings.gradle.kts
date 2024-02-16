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
    ":linkding:api",
    ":linkding:bind",
    ":data:models",
    ":data:bookmarks",
    ":data:configuration",
    ":data:unfurl",
    ":domain",
    ":ui:common:theme",
    ":ui:common:screens",
    ":ui:common:shared",
    ":ui:bookmarks",
    ":ui:add-bookmark",
    ":ui:setup",
    ":ui:root",
)
