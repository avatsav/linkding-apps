import dev.avatsav.gradle.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("convention.android.library")
    id("convention.kotlin.multiplatform")
    id("convention.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.avatsav.linkding.shared"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.base)
            api(projects.core.logging)
            api(projects.core.preferences)
            api(projects.core.connectivity)
            api(projects.data.linkdingApi)
            api(projects.data.bookmarks)
            api(projects.data.databaseSqldelight)
            api(projects.domain)
            api(projects.ui.root)
            api(projects.ui.theme)
            api(projects.features.setup)
            api(projects.features.bookmarks)
            api(projects.features.addBookmark)
            api(projects.features.settings)
            api(projects.features.tags)
            api(projects.kim.annotations)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        targets.withType<KotlinNativeTarget>().configureEach {
            binaries.framework {
                isStatic = true
                baseName = "LinkdingKt"
                export(projects.ui.root)
            }
        }
    }
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(projects.kim.compiler)
