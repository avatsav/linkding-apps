import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi

plugins {
    id("convention.kotlin.multiplatform")
    id("convention.compose")
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.app.shared)
                implementation(libs.kotlin.coroutines.swing)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.avatsav.linkding.MainKt"
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "dev.avatsav.linkding"
            packageVersion = "1.0.0"
        }
    }
}
