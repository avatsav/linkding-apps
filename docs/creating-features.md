# Creating New Features

Follow the **3-layer pattern** for all features.

## 1. API Module (`:features:{feature}:api`)

```kotlin
// build.gradle.kts
plugins {
  id("convention.kmp.lib")
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
  id("convention.kmp.lib")
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
  id("convention.kmp.lib")
  id("convention.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.features.{feature}.api)
      implementation(projects.core.presenter)
      implementation(projects.core.di)
      implementation(projects.ui.navigation)
      implementation(projects.ui.compose)
      implementation(libs.compose.lifecycle)
      implementation(libs.compose.retain)
    }
  }
}

android { namespace = "dev.avatsav.linkding.{feature}.ui" }
```

**File structure:**
```
{feature}/ui/
├── {Feature}Presenter.kt
├── {Feature}Screen.kt
├── {Feature}UiContract.kt
└── di/{Feature}ScreenComponent.kt
```

## 4. Implement Presenter, Screen, and Route

### Presenter (`{Feature}Presenter.kt`)

```kotlin
@Inject
class MyPresenter(
  private val repository: MyRepository,
) : MoleculePresenter<MyUiEvent, MyUiState, MyUiEffect>() {

  @Composable
  override fun models(events: Flow<MyUiEvent>): MyUiState {
    var data by rememberSaveable { mutableStateOf<Data?>(null) }
    var loading by remember { mutableStateOf(false) }

    ObserveEvents { event ->
      when (event) {
        MyUiEvent.Load -> presenterScope.launch {
          loading = true
          data = repository.load()
          loading = false
        }
      }
    }

    return MyUiState(loading = loading, data = data)
  }
}
```

### Screen (`{Feature}Screen.kt`)

```kotlin
@Composable
fun MyScreen(presenter: MyPresenter, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by presenter.models.collectAsStateWithLifecycle()

  ObserveEffects(presenter.effects) { effect ->
    when (effect) {
      MyUiEffect.NavigateBack -> navigator.pop()
    }
  }

  MyScreenContent(state, presenter::eventSink, modifier)
}
```

### Route Registration (`di/{Feature}ScreenComponent.kt`)

```kotlin
@ContributesTo(UserScope::class)
interface MyScreenComponent {
  @IntoSet @Provides
  fun provideEntry(presenter: Provider<MyPresenter>): RouteEntryProviderScope = {
    entry<Route.MyRoute> { MyScreen(retainedPresenter(presenter())) }
  }
}
```

## 5. Wire in App Module

Add to `:app:shared/build.gradle.kts`:

```kotlin
api(projects.features.{feature}.api)
api(projects.features.{feature}.impl)
api(projects.features.{feature}.ui)
```

## Using Route Parameters (AssistedInject)

If your presenter needs route parameters (IDs, URLs, etc.), use `@AssistedInject`:

### Presenter with Assisted Parameters

```kotlin
@AssistedInject
class EditItemPresenter(
  @Assisted private val itemId: Long,  // from route
  private val repository: ItemRepository,  // from DI
) : MoleculePresenter<EditItemUiEvent, EditItemUiState, EditItemUiEffect>() {

  @Composable
  override fun models(events: Flow<EditItemUiEvent>): EditItemUiState {
    // Use itemId to load data
    return EditItemUiState()
  }

  @AssistedFactory
  interface Factory {
    fun create(itemId: Long): EditItemPresenter
  }
}
```

### Route Registration with Factory

```kotlin
@ContributesTo(UserScope::class)
interface MyScreenComponent {
  @IntoSet @Provides
  fun provideEntry(factory: EditItemPresenter.Factory): RouteEntryProviderScope = {
    entry<Route.EditItem> { route ->
      EditItemScreen(retainedPresenter(factory.create(route.itemId)))
    }
  }
}
```

## Checklist

- [ ] API module has only interfaces
- [ ] UI depends only on API (not impl)
- [ ] Presenter uses `@Inject` (or `@AssistedInject` for route parameters)
- [ ] Route registered via `@IntoSet` with `Provider<Presenter>` or Factory injection
- [ ] Screen uses `retainedPresenter()` to retain presenter
- [ ] State uses `rememberSaveable` where persistence is needed
- [ ] Package: `dev.avatsav.linkding.{feature}.{layer}.*`
