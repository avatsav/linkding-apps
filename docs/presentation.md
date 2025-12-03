# Presentation Architecture

The app uses **MoleculeViewModel** - combining Jetpack ViewModel lifecycle with Molecule's Composable-based reactive state.

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
@AssistedInject
class MyPresenter(
  @Assisted coroutineScope: CoroutineScope,
  private val repository: Repository,
) : MoleculePresenter<MyUiEvent, MyUiState, MyUiEffect>(coroutineScope) {

  @Composable
  override fun models(events: Flow<MyUiEvent>): MyUiState {
    var data by remember { mutableStateOf<Data?>(null) }
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

  @AssistedFactory
  interface Factory {
    fun create(scope: CoroutineScope): MyPresenter
  }
}
```

### 3. ViewModel

Thin wrapper that provides lifecycle:

```kotlin
@ContributesIntoMap(UserScope::class, binding<ViewModel>())
@ViewModelKey(MyViewModel::class)
@Inject
class MyViewModel(presenterFactory: MyPresenter.Factory) :
  MoleculeViewModel<MyUiEvent, MyUiState, MyUiEffect>() {
  override val presenter by lazy { presenterFactory.create(viewModelScope) }
}
```

### 4. Screen

Compose UI with effect handling:

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by viewModel.models.collectAsStateWithLifecycle()

  ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
      MyUiEffect.NavigateBack -> navigator.pop()
      is MyUiEffect.ShowError -> { /* show snackbar */ }
    }
  }

  MyScreenContent(state, viewModel::eventSink, modifier)
}
```

## Key APIs

| API | Purpose |
|-----|---------|
| `models: StateFlow<Model>` | Reactive UI state |
| `effects: ReceiveChannel<Effect>` | One-time side effects |
| `eventSink(event)` | Send events to presenter |
| `ObserveEvents { }` | Handle events in presenter |
| `emitEffect(effect)` | Emit side effects |
| `presenterScope` | Coroutine scope for async work |
