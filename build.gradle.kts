import kotlin.jvm.java
import org.gradle.kotlin.dsl.libs

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.android.lint) apply false
  alias(libs.plugins.android.test) apply false

  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.ktfmt) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.metro) apply false
  alias(libs.plugins.gradleVersions) apply false
}

/** https://github.com/gradle/gradle/issues/33619 */
subprojects {
  tasks.withType(Test::class.java).configureEach { failOnNoDiscoveredTests.set(false) }
}
