import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi

plugins {
  id("convention.desktop.app")
  id("convention.compose")
}

dependencies {
  implementation(projects.app.shared)
  implementation(libs.kotlin.coroutines.swing)
  implementation(compose.desktop.currentOs)
}

compose.desktop {
  application {
    mainClass = "dev.avatsav.linkding.MainKt"
    nativeDistributions {
      targetFormats(Dmg, Msi, Deb)
      packageName = "dev.avatsav.linkding"
      packageVersion = "1.0.0"
      macOS { iconFile.set(file("icons/AppIcon.icns")) }
      linux { iconFile.set(file("icons/AppIcon.png")) }
      windows { iconFile.set(file("icons/AppIcon.ico")) }
    }
  }
}
