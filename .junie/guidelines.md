Project development guidelines (advanced)

This document captures project-specific knowledge to speed up development and CI debugging for linkding-apps. It assumes familiarity with Android/Kotlin Multiplatform/Gradle and focuses only on this repository’s specifics.

1) Build and configuration

- Toolchains
  - Java: JDK 24 (Zulu distribution is used in CI). Kotlin will target JVM_22 automatically where needed (you will see the “falling back to JVM_22” log; that’s expected).
  - Gradle: use the included wrapper (./gradlew). Gradle configuration cache is enabled project-wide.
  - Kotlin: 2.2.10. Compose Multiplatform: 1.8.2. AGP: 8.12.2. See gradle/libs.versions.toml for authoritative versions.
- Repository layout
  - Kotlin Multiplatform with many shared modules and Android/iOS/Desktop apps. Modules are declared in settings.gradle.kts with Typesafe Project Accessors enabled (use projects.<path> in Gradle scripts).
  - Centralized plugins via gradle/build-logic and version catalog via gradle/libs.versions.toml.
- Android SDK
  - compileSdk/targetSdk 36, minSdk 30 (see gradle/libs.versions.toml). Use Android Studio Koala+ and recent SDKs.
- iOS/Desktop
  - KMP targets are configured in modules as needed; desktop uses Compose Multiplatform. Xcode is required only if building iOS targets.
- Common build commands
  - Fast local checks: ./gradlew ktfmtCheck detektAll
  - Full CI parity: ./gradlew ktfmtCheck :build-logic:convention:ktfmtCheck detektAll testDebugUnitTest assembleDebug
  - Assemble Android debug APKs only: ./gradlew assembleDebug
  - Build all without running tests: ./gradlew build -x test
  - KMP module test aggregation (example): ./gradlew :core:connectivity:allTests

2) Testing

- Test frameworks
  - KMP unit tests primarily live in src/commonTest using kotlin.test APIs; modules add additional libs when needed (e.g., app.cash.turbine for Flows, Kotest assertions).
  - Android unit tests (testDebugUnitTest) are wired in CI; instrumentation tests are not currently part of CI.
- How to run tests
  - Run all Android unit tests (as CI does): ./gradlew testDebugUnitTest
  - Run tests for a specific KMP module (example): ./gradlew :core:connectivity:allTests
  - Run JVM tests only for a module (if needed): ./gradlew :<module>:jvmTest
  - Run all checks (format + static analysis + tests): ./gradlew ktfmtCheck detektAll testDebugUnitTest
- Adding tests to a KMP module
  - Place tests under <module>/src/commonTest/kotlin/... using kotlin.test.Test, or under <target>Test for target-specific logic (e.g., jvmTest, androidUnitTest).
  - If your module does not declare test dependencies, add them in build.gradle.kts, e.g.:
    kotlin {
      sourceSets {
        commonTest.dependencies {
          implementation(libs.kotlin.test)
          implementation(libs.kotlin.coroutines.test)
          implementation(libs.kotest.assertions)
          implementation(libs.turbine)
        }
      }
    }
  - Prefer commonTest when possible; target-specific tests should be minimal.
- Demonstrated test run (validated)
  - We verified that :core:connectivity common tests execute successfully via:
    ./gradlew :core:connectivity:allTests
  - Notes: If you see Kotlin falling back from JDK 24 to JVM_22 target in logs, it is expected with current Kotlin.

3) Static analysis and formatting

- ktfmt
  - Enforced via com.ncorti.ktfmt. Run checks with ./gradlew ktfmtCheck (and also for build-logic with :build-logic:convention:ktfmtCheck). Auto-format with ./gradlew ktfmtFormat.
- Detekt
  - Config at config/detekt. Run all rules with ./gradlew detektAll. Compose-specific detekt rules are enabled (io.nlopez.compose.rules:detekt).
- Compose stability/configuration
  - compose-stability.conf is present at root to help Compose compiler infer stability; keep it updated when adding stable models.

4) Dependency management and DI/codegen

- Version catalog
  - All versions and aliases are in gradle/libs.versions.toml. Prefer adding new dependencies there and using libs.<alias> in build scripts.
- Build logic
  - Shared Gradle conventions are implemented in gradle/build-logic. For example, convention.kotlin.multiplatform sets up common targets and options. Use these instead of duplicating setup.
- KSP / Kotlin Inject / Anvil
  - KSP is applied in several modules. Utility function addKspDependencyForAllTargets(...) wires KSP processors for all KMP targets. If you add a new module with DI, ensure you apply KSP and add processors for all targets to avoid missing generated code on non-JVM targets.

5) Database

- SQLDelight is used (app.cash.sqldelight). Generated sources are under data/database-sqldelight/build/generated/... during builds. When adding SQL, keep drivers aligned across targets (android, native, sqlite) and update adapters as needed.

6) Networking

- Ktor 3.x is used. Serialization is kotlinx.serialization. Keep client engines per target (OkHttp for JVM/Android, Darwin for iOS) configured in respective source sets.

7) Module-specific notes

- core/connectivity
  - Provides connectivity observation; tests rely on app.cash.turbine and Kotest assertions. Example test is ConnectivityObserverTest in src/commonTest.
- app/shared (+ Android/iOS/Desktop apps)
  - Compose Multiplatform UI. Android app builds with assembleDebug; desktop app uses Compose Desktop packaging if configured.

8) Local development tips

- JDK alignment
  - Use JDK 24 locally for parity with CI (Zulu recommended). If using another vendor, ensure toolchain configuration works; Gradle will still compile but target fallback logs may appear.
- Configuration cache
  - The project enables STABLE_CONFIGURATION_CACHE; avoid dynamicGradle features or reading System.getenv at configuration time in new modules to keep cache hits high.
- Running a subset of modules
  - For faster iteration, you can limit included tasks to a single module (e.g., :features:bookmarks:assembleDebug), or rely on Gradle’s --continue and configuration cache.

9) How to add and run a new simple test (example recipe)

- Example (KMP common test in a module that already declares test deps, e.g., core/connectivity):
  1) Create file core/connectivity/src/commonTest/kotlin/dev/avatsav/linkding/internet/SampleTest.kt with:
     package dev.avatsav.linkding.internet
     import kotlin.test.Test
     import kotlin.test.assertTrue
     class SampleTest { @Test fun itWorks() { assertTrue(2 * 2 == 4) } }
  2) Run: ./gradlew :core:connectivity:allTests
  3) Expect BUILD SUCCESSFUL. If not, ensure the module’s commonTest dependencies include libs.kotlin.test.
- We validated this workflow by running :core:connectivity:allTests locally; it passed.

10) CI overview

- Workflow: .github/workflows/ci.yml
  - Java 24 (Zulu), ktfmtCheck, detektAll, testDebugUnitTest, assembleDebug.
  - If local checks pass but CI fails, verify formatting/parsing differences (e.g., ktfmt plugin versions) and ensure no tasks depend on missing Android SDK components in headless mode.

11) When adding modules

- Add the module to settings.gradle.kts (include("::<path>")) and prefer reusing existing convention plugins from build-logic.
- For KMP modules that need tests, declare commonTest dependencies. If you forget, you’ll see unresolved references for kotlin.test on test compilation.

This file is project-specific and should be kept updated when CI scripts, toolchain versions, or module conventions change.
