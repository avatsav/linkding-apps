# AGENTS.md

Unofficial Android/iOS app for [Linkding](https://github.com/sissbruecker/linkding) built with
Kotlin Multiplatform and Compose Multiplatform.

## Stack

- **Compose Multiplatform - UI
- **Navigation 3** - Type-safe navigation
- **Molecule** - Composable-based state management
- **Metro DI** - Dependency injection
- **Ktor** - HTTP client
- **SQLDelight** - Database

## Commands

```bash
# Build & run
./gradlew :app:android:installDebug

# Format
./gradlew ktfmtFormat

# Lint single module
./gradlew :features:bookmarks:ui:detekt

# Lint all
./gradlew detektAll

# Test single module
./gradlew :core:connectivity:test
```

## Do

- Use **MoleculeViewModel** pattern for all screens (
  see [docs/presentation.md](docs/presentation.md))
- Follow **3-layer feature structure**: `api/` → `impl/` → `ui/`
- UI modules depend only on API modules, never impl
- Register screens via `@IntoSet` providers in `ScreenComponent`
- Use `ObserveEffects` for navigation and side effects
- Use appropriate DI scopes: `AppScope`, `UiScope`, `UserScope`
- Package names: `dev.avatsav.linkding.{feature}.{layer}.*`

## Don't

- Don't put implementations in API modules
- Don't import impl modules from UI modules
- Don't use Circuit (removed) - use MoleculeViewModel instead
- Don't hardcode colors/dimensions - use theme tokens
- Don't create monolithic components - keep them small and focused

## Documentation

- [Architecture](docs/architecture.md) - Module organization, DI scopes
- [Presentation](docs/presentation.md) - MoleculeViewModel pattern
- [Navigation](docs/navigation.md) - Navigation 3 setup
- [Creating Features](docs/creating-features.md) - Step-by-step guide

## Memory Imports

@docs/architecture.md
@docs/presentation.md
@docs/navigation.md
@docs/creating-features.md
