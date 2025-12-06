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

Register routes in `di/{Feature}ScreenComponent.kt`:

```kotlin
@ContributesTo(UiScope::class)
interface MyScreenComponent {
  @IntoSet @Provides
  fun provideEntry(): RouteEntryProviderScope = {
    entry<Route.MyRoute> { MyScreen(viewModel = metroViewModel()) }
  }
}
```

## URL Handling

`Route.Url` is intercepted and delegated to platform handlers (Chrome Custom Tabs on Android, system browser on Desktop).
