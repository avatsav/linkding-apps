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
    ":core:base",
    ":core:preferences",
    ":core:connectivity",
    ":core:parcelize",
    ":data:models",
    ":data:bookmarks",
    ":data:database",
    ":data:database-sqldelight",
    ":data:linkding-api",
    ":domain",
    ":ui:theme",
    ":ui:screens",
    ":ui:compose",
    ":ui:circuit",

    ":features:auth:ui",
    ":features:auth:api",
    ":features:auth:impl",

    ":features:bookmarks",
    ":features:settings",
    ":thirdparty:paging:compose",
    ":codegen:annotations",
    ":codegen:compiler",
)
