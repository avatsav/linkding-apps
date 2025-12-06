# Navigation

The app uses **Jetpack Navigation 3** for type-safe, Compose-first navigation.

## Setup

```kotlin
val backStack = rememberScreenBackStack(savedStateConfig, startScreen)
val navigator = rememberNavigator(backStack, onOpenUrl)

NavigatorCompositionLocals(navigator) {
  NavDisplay(
    backStack = backStack,
    onBack = { navigator.pop() },
    ...
  )
}
```

## Screens

Defined in `ui/navigation/Screens.kt`:

```kotlin
@Serializable
sealed interface Screen {
  val key: String  // Unique key for result routing

  @Serializable data class Auth(override val key: String = Uuid.random().toString()) : Screen
  @Serializable data class BookmarksFeed(override val key: String = Uuid.random().toString()) : Screen
  @Serializable data class AddBookmark(val sharedUrl: String? = null, override val key: String = ...) : Screen
  @Serializable data class Url(val url: String, override val key: String = ...) : Screen
  @Serializable data class Settings(override val key: String = ...) : Screen
  @Serializable data class Tags(val selectedTagIds: List<Long> = emptyList(), override val key: String = ...) : Screen
}
```

## ScreenBackStack

Type-safe backstack for managing screens:

```kotlin
val backStack = rememberScreenBackStack(savedStateConfig, Screen.Home())
backStack += Screen.Details(itemId)  // push
backStack.removeLast()               // pop
```

## Navigator

Access via `LocalNavigator.current`:

```kotlin
interface Navigator {
  fun goTo(screen: Screen): Boolean
  fun pop(result: NavResult? = null): Screen?
  fun resetRoot(newRoot: Screen): Boolean
}
```

## Screen Registration

Register screens in `di/{Feature}ScreenComponent.kt`:

```kotlin
@ContributesTo(UiScope::class)
interface MyScreenComponent {
  @IntoSet
  @Provides
  fun provideMyScreenEntry(): ScreenEntryProviderScope = {
    entry<Screen.MyScreen> {
      MyScreen(viewModel = metroViewModel())
    }
  }

  // With parameters
  @IntoSet
  @Provides
  fun provideDetailEntry(): ScreenEntryProviderScope = {
    entry<Screen.Detail> { screen ->
      DetailScreen(
        viewModel = assistedMetroViewModel<DetailViewModel, DetailViewModel.Factory> {
          create(screen.id)
        }
      )
    }
  }
}
```

## URL Handling

`Screen.Url` navigation is intercepted and delegated to platform-specific handlers:
- **Android**: Chrome Custom Tabs
- **Desktop**: System browser
