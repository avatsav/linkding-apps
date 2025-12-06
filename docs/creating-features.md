# Creating New Features

Follow the **3-layer pattern** for all features.

## 1. API Module (`:features:{feature}:api`)

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
    }
  }
}
```

**Contents:**
- Repository interfaces
- Interactors extending `Interactor<P,R,E>`
- Observers extending `Observer<P,T>`

## 2. Implementation Module (`:features:{feature}:impl`)

```kotlin
// build.gradle.kts
plugins {
  id("convention.kotlin.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.{feature}.api)
      implementation(projects.data.database)
      implementation(projects.data.linkdingApi)
    }
  }
}
```

**Contents:**
- Repository implementations with `@Inject`, `@ContributesBinding`
- DI components in `di/` package

## 3. UI Module (`:features:{feature}:ui`)

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
      implementation(projects.features.{feature}.api)
      implementation(projects.core.viewmodel)
      implementation(projects.core.di)
      implementation(projects.ui.navigation)
      implementation(projects.ui.compose)
      implementation(libs.compose.lifecycle)
      implementation(libs.metro.viewmodel.compose)
    }
  }
}

android { namespace = "dev.avatsav.linkding.{feature}.ui" }
```

**File structure:**
```
{feature}/ui/
├── {Feature}ViewModel.kt
├── {Feature}Screen.kt
├── {Feature}UiContract.kt
└── di/{Feature}ScreenComponent.kt
```

## 4. Wire in App Module

Add to `:app:shared/build.gradle.kts`:

```kotlin
api(projects.features.{feature}.api)
api(projects.features.{feature}.impl)
api(projects.features.{feature}.ui)
```

## Checklist

- [ ] API module has only interfaces
- [ ] UI depends only on API (not impl)
- [ ] ViewModel uses `@ContributesIntoMap` + `@ViewModelKey`
- [ ] Route registered via `@IntoSet` in ScreenComponent
- [ ] Package: `dev.avatsav.linkding.{feature}.{layer}.*`
