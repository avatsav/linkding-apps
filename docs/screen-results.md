# Route Results

Type-safe result passing between routes using `RouteWithResult` and `rememberResultNavigator`.

## Define a Result-Returning Route

```kotlin
@Serializable
data class Tags(val selectedTagIds: List<Long> = emptyList()) : Route, RouteWithResult<Tags.Result> {
  sealed interface Result : NavResult {
    @Serializable data class Confirmed(val selectedTags: List<Tag>) : Result
    @Serializable data object Dismissed : Result
  }
}
```

## Return Results

Use `navigator.pop(result)` from the target route's presenter effects:

```kotlin
ObserveEffects(presenter.effects) { effect ->
  when (effect) {
    is TagsUiEffect.Confirmed -> navigator.pop(Route.Tags.Result.Confirmed(effect.tags))
    TagsUiEffect.Dismiss -> navigator.pop(Route.Tags.Result.Dismissed)
  }
}
```

## Navigate and Receive Results

```kotlin
val tagsNavigator = rememberResultNavigator<Route.Tags, Route.Tags.Result> { result ->
  when (result) {
    is Route.Tags.Result.Confirmed -> presenter.eventSink(SetTags(result.selectedTags))
    Route.Tags.Result.Dismissed -> { }
  }
}

Button(onClick = { tagsNavigator(Route.Tags(selectedTagIds)) }) {
  Text("Select Tags")
}
```

## Bottom Sheets

Results work seamlessly with bottom sheets—delivered when the sheet dismisses:

```kotlin
entry<Route.Tags>(metadata = BottomSheetSceneStrategy.bottomSheetExpanded()) { route ->
  TagsScreen(retainedPresenter(factory.create(route.selectedTagIds)))
}
```
