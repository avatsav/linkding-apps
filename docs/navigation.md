# Navigation

Type-safe navigation using **Jetpack Navigation 3**.

## Setup

```kotlin
val backStack = rememberRouteBackStack(savedStateConfig, startRoute)
val navigator = rememberNavigator(backStack, onOpenUrl)

NavigatorCompositionLocals(navigator) {
  NavDisplay(backStack = backStack, onBack = { navigator.pop() })
}
```

## Routes

Defined in `ui/navigation/Routes.kt`. Use `data object` for parameterless routes, `data class` for routes with parameters:

```kotlin
@Serializable
sealed interface Route {
  val key: String get() = toString()

  @Serializable data object Auth : Route
  @Serializable data object Settings : Route
  @Serializable data class AddBookmark(val sharedUrl: String? = null) : Route
}
```

## Navigator

Access via `LocalNavigator.current`:

```kotlin
val navigator = LocalNavigator.current
navigator.goTo(Route.Settings)      // push
navigator.pop()                     // pop
navigator.pop(result)               // pop with result
navigator.resetRoot(Route.Auth)     // clear and set new root
```

## Route Registration

### Simple Routes (No Parameters)

For routes without parameters, use `Provider<Presenter>`:

```kotlin
@ContributesTo(UserScope::class)
interface MyScreenComponent {
  @IntoSet @Provides
  fun provideEntry(presenter: Provider<MyPresenter>): RouteEntryProviderScope = {
    entry<Route.MyRoute> { MyScreen(retainedPresenter(presenter())) }
  }
}
```

**Key points:**
- Inject `Provider<MyPresenter>` to get a factory for creating presenter instances
- Call `presenter()` to create a new instance for each route entry
- Wrap with `retainedPresenter()` to survive recomposition and configuration changes

### Routes with Parameters

For routes with parameters (IDs, URLs, etc.), use `@AssistedInject` and inject the factory:

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

The `entry` lambda receives the typed route, allowing you to pass parameters to the factory:

```kotlin
entry<Route.Tags> { route ->
  TagsScreen(retainedPresenter(factory.create(route.selectedTagIds)))
}
```

**Key points:**
- Inject the `Factory` (from `@AssistedFactory` in presenter)
- The `entry` lambda receives the typed route
- Call `factory.create()` with route parameters
- Wrap with `retainedPresenter()` as usual

## URL Handling

`Route.Url` is intercepted and delegated to platform handlers (Chrome Custom Tabs on Android, system browser on Desktop).
