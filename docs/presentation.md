# Presentation Architecture

The app uses **MoleculePresenter** with **retain** - combining Compose's `retain()` API for lifecycle management with Molecule's Composable-based reactive state.

## Core Components

### 1. UI Contract

Define state, events, and effects in `{Feature}UiContract.kt`:

```kotlin
// Immutable UI state
data class MyUiState(
  val loading: Boolean = false,
  val data: Data? = null,
)

// User actions
sealed interface MyUiEvent {
  data object Load : MyUiEvent
  data class Save(val value: String) : MyUiEvent
}

// One-time side effects (navigation, toasts)
sealed interface MyUiEffect {
  data object NavigateBack : MyUiEffect
  data class ShowError(val message: String) : MyUiEffect
}
```

### 2. Presenter

Handles state logic using Compose runtime:

```kotlin
@Inject
class MyPresenter(
  private val repository: Repository,
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
        is MyUiEvent.Save -> presenterScope.launch {
          repository.save(event.value)
          emitEffect(MyUiEffect.NavigateBack)
        }
      }
    }

    return MyUiState(loading = loading, data = data)
  }
}
```

### 3. Screen

Compose UI with presenter retention and effect handling:

```kotlin
@Composable
fun MyScreen(presenter: MyPresenter, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by presenter.models.collectAsStateWithLifecycle()

  ObserveEffects(presenter.effects) { effect ->
    when (effect) {
      MyUiEffect.NavigateBack -> navigator.pop()
      is MyUiEffect.ShowError -> { /* show snackbar */ }
    }
  }

  MyScreenContent(state, presenter::eventSink, modifier)
}
```

### 4. Route Registration

Register routes with presenter retention in `di/{Feature}ScreenComponent.kt`:

```kotlin
@ContributesTo(UserScope::class)
interface MyScreenComponent {
  @IntoSet @Provides
  fun provideEntry(presenter: Provider<MyPresenter>): RouteEntryProviderScope = {
    entry<Route.MyRoute> { MyScreen(retainedPresenter(presenter())) }
  }
}
```

The `retainedPresenter()` function uses Compose's `retain()` API to:
- Survive recomposition and configuration changes
- Automatically clean up (call `presenter.close()`) when retired
- Work seamlessly with navigation state restoration via `retain-nav3`

## Assisted Injection (Route Parameters)

Use `@AssistedInject` when your presenter needs route parameters (IDs, URLs, etc.):

```kotlin
@AssistedInject
class AddBookmarkPresenter(
  @Assisted private val route: Route.AddBookmark,
  private val repository: BookmarkRepository,  // regular DI
) : MoleculePresenter<AddBookmarkUiEvent, AddBookmarkUiState, AddBookmarkUiEffect>() {

  @Composable
  override fun models(events: Flow<AddBookmarkUiEvent>): AddBookmarkUiState {
    val bookmarkId = route.bookmarkId
    // ... use route parameters and repository
    return AddBookmarkUiState()
  }

  @AssistedFactory
  interface Factory {
    fun create(route: Route.AddBookmark): AddBookmarkPresenter
  }
}
```

**Route registration with factory:**

```kotlin
@ContributesTo(UserScope::class)
interface BookmarksScreenComponent {
  @IntoSet @Provides
  fun provideEntry(factory: AddBookmarkPresenter.Factory): RouteEntryProviderScope = {
    entry<Route.AddBookmark> { route ->
      AddBookmarkScreen(retainedPresenter(factory.create(route)))
    }
  }
}
```

**Key points:**
- `@Assisted` parameters come from route data
- Regular parameters are injected by DI
- Create an `@AssistedFactory` interface with a `create()` method
- Inject the factory (not `Provider<Presenter>`) in the screen component
- Call `factory.create(route)` or `factory.create(param)` with the route data

## Key APIs

| API | Purpose |
|-----|---------|
| `models: StateFlow<Model>` | Reactive UI state |
| `effects: ReceiveChannel<Effect>` | One-time side effects |
| `eventSink(event)` | Send events to presenter |
| `ObserveEvents { }` | Handle events in presenter |
| `emitEffect(effect)` | Emit side effects |
| `presenterScope` | Coroutine scope for async work |
| `retainedPresenter(presenter)` | Retain presenter across recomposition |
| `rememberSaveable` | Persist state across process death |
