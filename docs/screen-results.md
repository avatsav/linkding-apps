# Screen Results

The app uses a **type-safe result navigation** pattern inspired by Circuit's AnsweringNavigator. This enables compile-time safe result passing between screens with full type inference.

## Setup

The navigation system is set up using `rememberScreenBackStack`, `rememberNavigator`, and `NavigatorCompositionLocals`:

```kotlin
val backStack = rememberScreenBackStack(savedStateConfig, startScreen)
val navigator = rememberNavigator(backStack, onOpenUrl)

NavigatorCompositionLocals(navigator) {
  // Your app content - LocalNavigator and result handling are available
  NavDisplay(backStack = backStack, ...)
}
```

## Core Components

### ScreenWithResult

Marker interface for screens that return typed results:

```kotlin
interface ScreenWithResult<R : NavResult>
```

### rememberResultNavigator

Creates a type-safe navigator constrained to a specific screen type:

```kotlin
@Composable
inline fun <reified S, reified R : NavResult> rememberResultNavigator(
  noinline onResult: (R) -> Unit
): ScreenNavigator<S> where S : Screen, S : ScreenWithResult<R>
```

### ScreenNavigator

A navigator constrained to a specific screen type:

```kotlin
interface ScreenNavigator<in S : Screen> {
  fun goTo(screen: S): Boolean
  operator fun invoke(screen: S): Boolean = goTo(screen)
}
```

## Usage

### 1. Define a Result-Returning Screen

In `Screens.kt`, define a screen that implements `ScreenWithResult`:

```kotlin
@Serializable
data class Tags(
  val selectedTagIds: List<Long> = emptyList(),
  override val key: String = Uuid.random().toString(),
) : Screen, ScreenWithResult<Tags.Result> {

  sealed interface Result : NavResult {
    @Serializable data class Confirmed(val selectedTags: List<Tag>) : Result
    @Serializable data object Dismissed : Result
  }
}
```

### 2. Return Results from the Target Screen

Use `navigator.pop(result)` to return a result when the screen closes:

```kotlin
@Composable
fun TagsScreen(viewModel: TagsViewModel) {
  val navigator = LocalNavigator.current

  ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
      is TagsUiEffect.TagsConfirmed -> {
        navigator.pop(Screen.Tags.Result.Confirmed(effect.selectedTags))
      }
      TagsUiEffect.Dismiss -> {
        navigator.pop(Screen.Tags.Result.Dismissed)
      }
    }
  }

  // ... rest of the screen
}
```

### 3. Navigate and Receive Results

Use `rememberResultNavigator` to create a type-safe navigator:

```kotlin
@Composable
fun BookmarksScreen(viewModel: BookmarksViewModel) {
  val tagsNavigator = rememberResultNavigator<Screen.Tags, Screen.Tags.Result> { result ->
    when (result) {
      is Screen.Tags.Result.Confirmed -> {
        viewModel.eventSink(BookmarkSearchUiEvent.SetTags(result.selectedTags))
      }
      Screen.Tags.Result.Dismissed -> {
        // Handle dismissal if needed
      }
    }
  }

  // Navigate using invoke operator
  Button(onClick = { tagsNavigator(Screen.Tags(selectedTagIds)) }) {
    Text("Select Tags")
  }

  // Or using goTo method
  Button(onClick = { tagsNavigator.goTo(Screen.Tags(selectedTagIds)) }) {
    Text("Select Tags")
  }

  // This would NOT compile - type safety!
  // tagsNavigator(Screen.Settings()) // Error: Type mismatch
}
```

## Type Safety Benefits

The pattern provides compile-time guarantees:

1. **Navigation constraints**: `ScreenNavigator<S>` only allows navigation to screens of type `S`
2. **Result type matching**: The result callback receives exactly the type declared by `ScreenWithResult<R>`
3. **No type erasure issues**: Results are defined as sealed interfaces, avoiding generic type erasure

## How It Works

1. When you call `tagsNavigator(Screen.Tags(...))`, the navigator:
   - Registers that the current screen expects a result
   - Navigates to the Tags screen

2. When the Tags screen calls `navigator.pop(result)`:
   - The result is stored in `NavigationResultHandler`
   - Navigation returns to the previous screen

3. When the calling screen becomes active:
   - `rememberResultNavigator` retrieves the pending result
   - The `onResult` callback is invoked with the typed result

## Bottom Sheets

Results work seamlessly with bottom sheets:

```kotlin
// In ScreenComponent
entry<Screen.Tags>(metadata = BottomSheetSceneStrategy.bottomSheetExpanded()) { screen ->
  TagsScreen(viewModel = ...)
}
```

The result is delivered when the bottom sheet dismisses and the calling screen becomes active again.
