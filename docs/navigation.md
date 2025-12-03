# Navigation

The app uses **Jetpack Navigation 3** for type-safe, Compose-first navigation.

## Screens

Defined in `ui/navigation/Screens.kt`:

```kotlin
sealed interface Screen : NavKey {
  @Serializable data object Auth : Screen
  @Serializable data object BookmarksFeed : Screen
  @Serializable data class AddBookmark(val sharedUrl: String? = null) : Screen
  @Serializable data class Url(val url: String) : Screen
  @Serializable data object Settings : Screen
  @Serializable data object Tags : Screen
}
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
