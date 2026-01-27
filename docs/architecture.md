# Architecture

## Module Organization

### Core Modules

| Module | Purpose |
|--------|---------|
| `:core:base` | `Interactor<P,R,E>`, `Observer<P,T>`, `AppCoroutineDispatchers` |
| `:core:di` | Scopes (`AppScope`, `UiScope`, `UserScope`), `GraphHolder` |
| `:core:presenter` | `MoleculePresenter`, `ObserveEffects`, `retainedPresenter()` |
| `:core:preferences` | Cross-platform preferences via `multiplatform-settings` |
| `:core:connectivity` | Network state monitoring |

### Data Modules

| Module | Purpose |
|--------|---------|
| `:data:models` | Domain models and data classes |
| `:data:database` | Database abstraction layer |
| `:data:database-sqldelight` | SQLDelight implementation |
| `:data:linkding-api` | Ktor-based HTTP client |

### UI Modules

| Module | Purpose |
|--------|---------|
| `:ui:theme` | Material 3 theming |
| `:ui:navigation` | Navigation 3 integration, `Navigator`, `Screen` |
| `:ui:compose` | Reusable Compose components |

### App Modules

| Module | Purpose |
|--------|---------|
| `:app:shared` | Shared app logic, `AppUi`, DI wiring |
| `:app:android` | Android entry point, Custom Tabs |
| `:app:desktop` | Desktop entry point |

## Feature Module Structure (3-Layer Pattern)

```
features/{feature}/
├── api/        # Interfaces, Interactors, Observers
├── impl/       # Repository implementations, DI bindings
└── ui/         # Presenter, Screen, UiContract
```

### Dependency Flow

```
UI Layer (app/*, features/*/ui)
         │
         ▼ (only API contracts)
API Layer (features/*/api)
         ▲
         │ (implements)
Implementation Layer (features/*/impl)
```

**Rules:**
1. UI depends only on API (never impl)
2. Implementation implements API contracts
3. API has no implementations

## Metro DI Scopes

| Scope | Lifecycle | Contains |
|-------|-----------|----------|
| `AppScope` | App lifetime | Preferences, API clients |
| `UiScope` | Unauthenticated | Login flow, screen registry |
| `UserScope` | Authenticated | User session, repositories |
