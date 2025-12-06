package dev.avatsav.linkding.navigation

import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.data.model.Tag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Sealed interface representing all navigable screens in the app.
 *
 * Each screen type is serializable for state restoration. Every screen instance has a [key]
 * property (defaulting to `toString()`) that enables result routing between screens.
 *
 * ## Result-Returning Screens
 *
 * Screens that return results implement [ScreenWithResult] and define their result types as nested
 * sealed interfaces. See [Tags] for an example.
 *
 * ## Usage
 *
 * ```kotlin
 * // Simple navigation
 * navigator.goTo(Screen.Settings)
 *
 * // Navigation with parameters
 * navigator.goTo(Screen.AddBookmark(sharedUrl = "https://example.com"))
 *
 * // Result navigation
 * val tagsNavigator = rememberResultNavigator<Screen.Tags, Screen.Tags.Result> { result ->
 *   when (result) {
 *     is Screen.Tags.Result.Confirmed -> handleTags(result.selectedTags)
 *     Screen.Tags.Result.Dismissed -> { }
 *   }
 * }
 * tagsNavigator(Screen.Tags(selectedTagIds))
 * ```
 *
 * @see Navigator
 * @see ScreenWithResult
 * @see rememberResultNavigator
 */
@Serializable
sealed interface Screen {
  /**
   * Key for this screen instance, used for result routing.
   *
   * Defaults to `toString()`, which provides a content-based key. Override this property if you
   * need unique keys for screens with identical parameters on the same back stack.
   */
  val key: String
    get() = toString()

  @Serializable data object Auth : Screen

  @Serializable data object BookmarksFeed : Screen

  @Serializable data class AddBookmark(val sharedUrl: String? = null) : Screen

  @Serializable data class Url(val url: String) : Screen

  @Serializable data object Settings : Screen

  @Serializable
  data class Tags(val selectedTagIds: List<Long> = emptyList()) :
    Screen, ScreenWithResult<Tags.Result> {

    sealed interface Result : NavResult {
      @Serializable data class Confirmed(val selectedTags: List<Tag>) : Result

      @Serializable data object Dismissed : Result
    }
  }
}

@ContributesTo(AppScope::class)
interface NavigationComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedStateConfiguration(): SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
      polymorphic(Screen::class) {
        subclass(Screen.Auth::class)
        subclass(Screen.BookmarksFeed::class)
        subclass(Screen.AddBookmark::class)
        subclass(Screen.Url::class)
        subclass(Screen.Settings::class)
        subclass(Screen.Tags::class)
      }
    }
  }
}

/**
 * Marker interface for screens that return a typed result.
 *
 * Implement this interface on [Screen] subclasses that can return results when popped. The type
 * parameter [R] specifies the result type, enabling compile-time type safety when using
 * [rememberResultNavigator].
 *
 * Example:
 * ```kotlin
 * @Serializable
 * data class Tags(
 *   val selectedTagIds: List<Long> = emptyList(),
 *   override val key: String = Uuid.random().toString(),
 * ) : Screen, ScreenWithResult<Tags.Result> {
 *
 *   sealed interface Result : NavResult {
 *     data class Confirmed(val selectedTags: List<Tag>) : Result
 *     data object Dismissed : Result
 *   }
 * }
 * ```
 *
 * @param R The result type this screen can return (must extend [NavResult])
 * @see NavResult
 * @see rememberResultNavigator
 */
interface ScreenWithResult<R : NavResult>
