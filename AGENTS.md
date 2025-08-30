# AGENTS.md - Development Context for Linkding Apps

This document provides essential development context for agentic coding tools working on the
Linkding Apps project. This is an unofficial Android/iOS app for
the [Linkding](https://github.com/sissbruecker/linkding) self-hosted bookmarking service, built with
Kotlin Multiplatform and Compose Multiplatform.

## Build System & Configuration

### Gradle Build Logic

- **Custom Convention Plugins**: Located in `gradle/build-logic/convention/`
    - `convention.kotlin.multiplatform` - Standard KMP setup
    - `convention.compose` - Compose Multiplatform configuration
    - `convention.android.application` - Android app configuration
    - `convention.android.library` - Android library configuration
- **Version Catalog**: `gradle/libs.versions.toml` - Centralized dependency management
- **Gradle Configuration**:
    - Configuration cache enabled with warnings
    - Parallel builds enabled
    - JVM args: `-Xmx4g -Dfile.encoding=UTF-8`
    - Uses JVM Toolchain 24 (falls back to 22 for Kotlin compatibility)

### Multiplatform Targets

- **Android**: Primary target with minSdk 30, targetSdk 36, compileSdk 36
- **iOS**: Supported via `org.jetbrains.compose.experimental.uikit.enabled=true`
- **JVM/Desktop**: Supported for development and testing

### Key Build Commands

```bash
# Build all targets
./gradlew build

# Run specific module tests
./gradlew :core:connectivity:test

# Run Android app
./gradlew :app:android:installDebug

# Format code
./gradlew ktfmtFormat

# Run detekt linting per module
./gradlew :feature:bookmarks:detekt

# Run detekt linting for the entire project
./gradlew detektAll

```

## Project Architecture

### Module Organization

#### Core Modules (Non-Feature)

- **`:core:base`** - Foundation utilities, DI annotations, coroutines, logging
    - Exports: `kotlin-inject`, `anvil`, `kotlinResult`, `coroutines`, `kermit`
    - Base classes for DI scopes and common utilities
- **`:core:preferences`** - App preferences and settings management
    - Uses `multiplatform-settings` for cross-platform storage
    - Exports `AppPreferences` interface with implementations
- **`:core:connectivity`** - Network connectivity monitoring
    - Cross-platform network state observation
    - Platform-specific implementations (iOS/JVM/Android)
- **`:core:parcelize`** - Parcelize support for multiplatform

#### Data Layer

- **`:data:models`** - Shared data models
- **`:data:bookmarks`** - Bookmark domain logic
- **`:data:database`** - Database abstraction
- **`:data:database-sqldelight`** - SQLDelight implementation
- **`:data:linkding-api`** - API client for Linkding service

#### UI Layer

- **`:ui:theme`** - Design system and theming
- **`:ui:screens`** - Screen definitions and navigation models
- **`:ui:compose`** - Reusable Compose components
- **`:ui:circuit`** - Circuit-specific utilities and extensions

#### App Layer

- **`:app:shared`** - Shared application logic across platforms
- **`:app:android`** - Android application module
- **`:app:desktop`** - Desktop application module

#### Domain Layer

- **`:domain`** - Domain logic and use cases

### Feature Modules Pattern

Feature modules follow a **3-layer API pattern**:

1. **`:features:{feature}:api`** - Public contracts and interfaces
    - Example: `AuthRepository`, `AuthManager` interfaces
    - No implementation details, only contracts

2. **`:features:{feature}:impl`** - Implementation of the API contracts
    - Business logic implementation
    - Repository implementations
    - Use cases and domain logic

3. **`:features:{feature}:ui`** - UI components and presentation logic
    - Circuit Presenters and UI factories
    - Compose UI implementations
    - Screen-specific state management

**Current Feature Modules:**

- **`features:auth`** - Authentication flow (login, API key management)
- **`features:bookmarks`** - Bookmark management and display
- **`features:settings`** - App settings and preferences

### Dependency Injection Architecture

Uses **kotlin-inject + kotlin-inject-anvil** for compile-time DI:

#### DI Scopes

- **`AppScope`** - Application-wide singletons
- **`UiScope`** - UI layer scope (unauthenticated state)
- **`UserScope`** - User session scope (authenticated state)

#### Key DI Components

- **`UserComponent`** - Manages authenticated user session
- **`CircuitComponent`** - Provides Circuit instances for authenticated/unauthenticated states
- Uses `@ContributesBinding`, `@ContributesTo`, and `@CircuitInject` annotations

#### Circuit Integration

- **Authenticated Circuit**: Used when user is logged in (`@Authenticated`)
- **Unauthenticated Circuit**: Used for login flow (`@Unauthenticated`)
- Presenters and UIs automatically discovered via `@CircuitInject`

## Testing Infrastructure

### Test Framework Stack

- **kotlin-test** - Multiplatform testing framework
- **kotlinx-coroutines-test** - Coroutine testing utilities
- **turbine** - Flow testing library
- **kotest-assertions** - Assertion library

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :core:connectivity:test

# Run Android unit tests only
./gradlew :core:connectivity:testDebugUnitTest

# Run with explicit rebuild
./gradlew :core:connectivity:test --rerun-tasks
```

### Test Structure

- Tests located in `src/commonTest/kotlin/` for multiplatform modules
- Platform-specific tests in `src/{platform}Test/kotlin/`
- Example test pattern demonstrated in `ConnectivityObserverTest`

### Creating New Tests

1. Place in appropriate `src/commonTest/kotlin/` directory
2. Use `kotlin.test.Test` annotation
3. Follow existing patterns for coroutine testing with `TestScope`
4. Example test case available in `core/connectivity/src/commonTest/kotlin/`

## Code Style & Quality

### Formatting

- **ktfmt** with Google Style
- Automatic trailing comma management
- Unused import removal
- Configuration in `gradle/build-logic/convention/src/main/kotlin/dev/avatsav/gradle/Ktfmt.kt`

### Linting

- **Detekt** for static analysis
- **Compose Rules** for Compose-specific linting
- JVM target: 21 for detekt

### Compose Stability

- Stability configuration in `compose-stability.conf`
- Treats Kotlin collections, `kotlinx.coroutines.CoroutineScope`, and data layer classes as stable

## Development Tools & Libraries

### Major Dependencies

- **Compose Multiplatform 1.8.2** - UI framework
- **Circuit 0.30.0** - Presentation architecture
- **Kotlin 2.2.10** with Compose compiler
- **kotlin-inject 0.8.0** - Dependency injection
- **Ktor 3.2.3** - HTTP client
- **SQLDelight 2.1.0** - Database
- **kotlinx-serialization** - JSON handling

### Platform-Specific

- **Android**: Activity Compose, Core KTX, Browser support
- **iOS**: Circuit gesture navigation, UIKit integration
- **Desktop/JVM**: Coroutines Swing support

## Additional Notes

### Configuration Cache

- Enabled with warning level for problems
- Helps with build performance
- Use `--configuration-cache` flag if needed

### KSP (Kotlin Symbol Processing)

- Used for code generation (DI, Circuit, etc.)
- Applied to all multiplatform targets via `addKspDependencyForAllTargets()`

### Inspiration & Credits

- Architecture heavily inspired by [Tivi](https://github.com/chrisbanes/tivi)
- Uses modern Android development patterns adapted for multiplatform

This project serves as both a functional app and an exploration of modern Kotlin Multiplatform
architecture, dependency injection patterns, and multiplatform UI development.
