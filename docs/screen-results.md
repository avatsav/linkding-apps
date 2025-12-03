# Screen Results

The app uses **ResultEventBus** for passing results between screens. This is useful when a screen needs to return data to a previous screen (e.g., selection from a bottom sheet).

## Core Components

### ResultEventBus

Access via `LocalResultEventBus.current`:

```kotlin
interface ResultEventBus {
  fun getResultFlow(resultKey: String): Flow<Any?>
  fun sendResult(resultKey: String, result: Any?)
  fun removeResult(resultKey: String)
}
```

### ResultEffect

A composable effect for receiving results:

```kotlin
@Composable
inline fun <reified T> ResultEffect(
  resultEventBus: ResultEventBus = LocalResultEventBus.current,
  resultKey: String = T::class.toString(),
  onResult: suspend (T) -> Unit,
)
```

## Usage

### 1. Define a Result Type

Create a wrapper class to avoid type erasure issues with generics:

```kotlin
// In {Feature}UiContract.kt
@Immutable
data class TagsSelectionResult(val selectedTags: List<Tag>)
```

### 2. Send Results

From the screen that produces the result:

```kotlin
@Composable
fun TagsScreen(viewModel: TagsViewModel) {
  val navigator = LocalNavigator.current
  val resultEventBus = LocalResultEventBus.current

  ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
      is TagsUiEffect.TagsConfirmed -> {
        resultEventBus.sendResult(TagsSelectionResult(effect.selectedTags))
        navigator.pop()
      }
    }
  }
}
```

### 3. Receive Results

In the screen that needs the result:

```kotlin
@Composable
fun BookmarksScreen(viewModel: BookmarksViewModel) {
  ResultEffect<TagsSelectionResult> { result ->
    viewModel.eventSink(BookmarkSearchUiEvent.SetTags(result.selectedTags))
  }

  // ... rest of the screen
}
```

## Important Notes

- **Use wrapper classes**: Avoid using generic types like `List<Tag>` directly as result types due to type erasure. Always wrap in a data class.
- **Cleanup is automatic**: `ResultEffect` automatically cleans up the channel when the composable leaves composition.
- **Import extension functions**: When using `sendResult` with type inference, import the extension function:
  ```kotlin
  import dev.avatsav.linkding.navigation.sendResult
  ```

## Bottom Sheets

Results work seamlessly with bottom sheets. The channel-based approach ensures results are buffered and delivered even when the bottom sheet overlay closes:

```kotlin
// In ScreenComponent
entry<Screen.Tags>(metadata = BottomSheetSceneStrategy.bottomSheetExpanded()) { screen ->
  TagsScreen(viewModel = ...)
}
```
