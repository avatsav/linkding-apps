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
-

### Multiplatform Targets

- **Android**: Primary target
- **iOS**: iOS target (not yet supported)
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
./gradlew :features:bookmarks:ui:detekt

# Run detekt linting for the entire project
./gradlew detektAll

```

## Project Architecture

The project follows a **clean modular architecture** with strict separation of concerns, leveraging
the **3-layer feature pattern** for scalable and maintainable code organization.

### Module Organization

#### Core Infrastructure Modules

- **`:core:base`** - Foundation layer
  - **Base Classes**: `Interactor<P,R,E>`, `Observer<P,T>`, `PagedObserver<P,T>`
  - **DI Infrastructure**: Scopes (`AppScope`, `UiScope`, `UserScope`)
  - **Utilities**: `AppCoroutineDispatchers`, `ComponentHolder`, initializers
- **`:core:preferences`** - Cross-platform preferences. Uses `multiplatform-settings` for storage
  abstraction
- **`:core:connectivity`** - Network state monitoring

- **`:core:parcelize`** - Multiplatform parcelize support

#### Data Layer

- **`:data:models`** - Domain models and data classes
- **`:data:database`** - Database abstraction layer
- **`:data:database-sqldelight`** - SQLDelight implementation with DAOs
- **`:data:linkding-api`** - HTTP client for Linkding REST API
  - **Implementation**: Ktor-based API client with serialization

#### UI Infrastructure

- **`:ui:theme`** - Design system and Material 3 theming
- **`:ui:screens`** - Screen definitions for navigation
- **`:ui:compose`** - Reusable Compose components and utilities
- **`:ui:circuit`** - Circuit framework extensions and utilities

#### App Layer

- **`:app:shared`** - Shared application logic across all platforms
  - **Main UI**: `AppUi` with theme management and navigation
  - **DI Integration**: Component wiring for authenticated/unauthenticated states
  - **Dependencies**: All feature modules (api + impl + ui)

- **`:app:android`** - Android-specific application
  - **MainActivity**: Launch mode handling, deep links, custom tabs
  - **Platform Integration**: Edge-to-edge, splash screen, intent handling

- **`:app:desktop`** - JVM/Desktop application
  - **Main**: Compose Desktop integration with window management

### 🏗️ Feature Modules: 3-Layer Architecture

**All features follow a strict 3-layer pattern** for clean separation of concerns:

#### Layer 1: API Module (`:features:{feature}:api`)

**Purpose**: Define contracts and business interfaces

- **Repository Interfaces**: Data access contracts (e.g., `BookmarksRepository`, `TagsRepository`)
- **Interactors**: Business use cases extending `Interactor<P,R,E>` (e.g., `AddBookmark`,
  `DeleteBookmark`)
- **Observers**: Data observation use cases extending `Observer<P,T>` (e.g., `ObserveBookmarks`)
- **Package**: `dev.avatsav.linkding.{feature}.api.*`
- **Dependencies**: Only `:core:base`, `:data:models`, and framework libraries
- **Rule**: **No implementations, only interfaces and contracts**

#### Layer 2: Implementation Module (`:features:{feature}:impl`)

**Purpose**: Provide concrete business logic and data access

- **Repository Implementations**: Concrete data access (API + local storage)
- **Business Logic**: Internal services, mappers, validators
- **Data Sources**: Paging sources, remote mediators, caching strategies
- **DI Components**: Metro DI binding and providing logic
- **Package**: `dev.avatsav.linkding.{feature}.impl.*`
- **Dependencies**: API module, data layer, framework libraries
- **Rule**: **Implements API contracts, no UI dependencies**

#### Layer 3: UI Module (`:features:{feature}:ui`)

**Purpose**: Handle presentation logic and user interface

- **Presenters**: Circuit presenters with business logic coordination
- **Screens**: Compose UI implementations and state management
- **UI States**: Screen state definitions and UI models
- **Navigation**: Screen routing and parameter mapping
- **Package**: `dev.avatsav.linkding.{feature}.ui.*`
- **Dependencies**: **Only API module** (never implementation directly)
- **Rule**: **UI → API interface contracts only**

### 🔄 Dependency Flow & Architecture Rules

```
┌─────────────────────────────────────────┐
│                 UI Layer                │  
│  ┌─────────────┐ ┌─────────────────────┐ │
│  │   :app:*    │ │ :features:*:ui      │ │
│  └─────────────┘ └─────────────────────┘ │
└─────────────────────┬───────────────────────┘
                      │ (only API contracts)
┌─────────────────────▼───────────────────────┐
│                API Layer                    │
│  ┌─────────────────────────────────────────┐ │  
│  │        :features:*:api                  │ │
│  │     (interfaces & contracts)            │ │
│  └─────────────────────────────────────────┘ │
└─────────────────┬───────────────────────────┘
                  ▲ (implements)
┌─────────────────▼───────────────────────────┐
│           Implementation Layer              │
│  ┌─────────────────────────────────────────┐ │
│  │       :features:*:impl                  │ │  
│  │    (business logic & data)              │ │
│  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

**Architecture Rules:**

1. **UI** depends only on **API interfaces** (never implementations)
2. **Implementation** modules implement **API contracts**
3. **API** modules define business contracts (no implementations)
4. **Base classes** (`Interactor`, `Observer`) live in `:core:base`
5. **Data models** centralized in `:data:models`
6. **Clean dependency flow**: UI → API ← Implementation

### 🔧 Metro DI Integration

**Scopes & Lifecycle:**

- **`AppScope`**: App-wide singletons (preferences, API clients)
- **`UiScope`**: Unauthenticated state (login flow)
- **`UserScope`**: Authenticated state (user session)

**Circuit Integration:**

- **Auto-discovery**: `@CircuitInject` presenters and UIs

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
5.

## Development Tools & Libraries

### Major Dependencies

- **Compose Multiplatform 1.8.2** - UI framework
- **Circuit 0.30.0** - Presentation architecture
- **Kotlin 2.2.10** with Compose compiler
- **Metro DI 0.6.3** - Dependency injection
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

## Feature Modularization Guidelines

### Creating New Features

When adding new features, follow the established **3-layer pattern**:

#### 1. Create API Module (`:features:{feature}:api`)

```kotlin
// build.gradle.kts
plugins {
  id("convention.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      api(projects.data.models)
      // Only interface dependencies
    }
  }
}
```

**Structure:**

- Repository interfaces extending base contracts
- Interactors extending `Interactor<P,R,E>` from `:core:base`
- Observers extending `Observer<P,T>` from `:core:base`
- Package: `dev.avatsav.linkding.{feature}.api.*`

#### 2. Create Implementation Module (`:features:{feature}:impl`)

```kotlin
// build.gradle.kts  
plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.{ feature }.api)
      implementation(projects.data.database)
      implementation(projects.data.linkdingApi)
      // Implementation dependencies
    }
  }
}

ksp { arg("circuit.codegen.mode", "metro") }
addKspDependencyForAllTargets(libs.circuit.codegen)
```

**Structure:**

- Repository implementations with `@Inject` and `@ContributesBinding`
- Business logic, mappers, validators
- DI components extending from API interfaces
- Package: `dev.avatsav.linkding.{feature}.impl.*`

#### 3. Create UI Module (`:features:{feature}:ui`)

```kotlin
// build.gradle.kts
plugins {
  id("convention.android.library")
  id("convention.kotlin.multiplatform")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.{ feature }.api) // Only API!
      implementation(projects.ui.navigation)
      implementation(projects.ui.compose)
      // UI dependencies
    }
  }
}

android { namespace = "dev.avatsav.linkding.{feature}.ui" }
ksp { arg("circuit.codegen.mode", "metro") }
addKspDependencyForAllTargets(libs.circuit.codegen)
```

**Structure:**

- Circuit presenters with `@CircuitInject`
- Compose screens and UI components
- UI state definitions
- Package: `dev.avatsav.linkding.{feature}.ui.*`
- **Rule**: Only import from API module, never implementation

#### 4. Wire in App Module

Add dependencies to `:app:shared/build.gradle.kts`:

```kotlin
api(projects.features.{ feature }.api)
api(projects.features.{ feature }.impl)
api(projects.features.{ feature }.ui)
```

### Architecture Validation Checklist

- [ ] **API Module**: Only interfaces, no implementations
- [ ] **Implementation Module**: Concrete business logic, implements API contracts
- [ ] **UI Module**: Only depends on API interfaces, uses `@CircuitInject`
- [ ] **Package Names**: Follow `dev.avatsav.linkding.{feature}.{layer}.*` convention
- [ ] **DI Integration**: KSP configured with `circuit.codegen.mode=metro`
- [ ] **Build Independence**: Each module compiles independently
- [ ] **Clean Dependencies**: UI → API ← Implementation (no UI → Impl)

### Common Patterns

- **Repository Pattern**: API defines contracts, Impl provides concrete data access
- **Interactor Pattern**: Business use cases with loading states and error handling
- **Observer Pattern**: Reactive data observation with Flow-based APIs
- **DI Scopes**: Use appropriate Metro DI scopes (App/UI/User)
- **Error Handling**: Consistent `Result<T,E>` patterns across all layers
