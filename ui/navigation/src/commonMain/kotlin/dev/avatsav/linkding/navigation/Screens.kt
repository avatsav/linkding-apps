package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.data.model.Tag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Sealed interface representing all navigable screens in the app.
 *
 * Each screen type is serializable for state restoration and implements [NavKey] for Navigation 3
 * compatibility. Every screen instance has a unique [key] property (defaulting to a random UUID)
 * that enables result routing between screens.
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
 * navigator.goTo(Screen.Settings())
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
@OptIn(ExperimentalUuidApi::class)
@Serializable
sealed interface Screen : NavKey {
  /**
   * Unique key for this screen instance.
   *
   * Used for result routing - when a screen pops with a result, this key identifies which caller
   * should receive the result. Defaults to a random UUID to ensure each screen instance is uniquely
   * identifiable.
   */
  val key: String

  /** Authentication/login screen. */
  @Serializable data class Auth(override val key: String = Uuid.random().toString()) : Screen

  /** Main bookmarks feed screen. */
  @Serializable
  data class BookmarksFeed(override val key: String = Uuid.random().toString()) : Screen

  /**
   * Screen for adding a new bookmark.
   *
   * @property sharedUrl Optional URL shared from another app
   */
  @Serializable
  data class AddBookmark(
    val sharedUrl: String? = null,
    override val key: String = Uuid.random().toString(),
  ) : Screen

  /**
   * Virtual screen for opening URLs.
   *
   * Navigation to this screen is intercepted and handled by the platform's URL handler (e.g.,
   * Chrome Custom Tabs on Android, system browser on desktop).
   *
   * @property url The URL to open
   */
  @Serializable
  data class Url(val url: String, override val key: String = Uuid.random().toString()) : Screen

  /** App settings screen. */
  @Serializable data class Settings(override val key: String = Uuid.random().toString()) : Screen

  /**
   * Tag selection screen that returns selected tags as a result.
   *
   * This screen demonstrates the result-returning pattern. It implements [ScreenWithResult] to
   * declare its result type, and the calling screen uses [rememberResultNavigator] to receive the
   * result when this screen is popped.
   *
   * @property selectedTagIds IDs of initially selected tags
   */
  @Serializable
  data class Tags(
    val selectedTagIds: List<Long> = emptyList(),
    override val key: String = Uuid.random().toString(),
  ) : Screen, ScreenWithResult<Tags.Result> {

    /** Result types for the tag selection screen. */
    sealed interface Result : NavResult {
      /** User confirmed selection with the given tags. */
      @Serializable @Immutable data class Confirmed(val selectedTags: List<Tag>) : Result

      /** User dismissed without selecting. */
      @Serializable @Immutable data object Dismissed : Result
    }
  }
}

@ContributesTo(AppScope::class)
interface NavigationComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedStateConfiguration(): SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
      polymorphic(NavKey::class) {
        subclass(Screen.Auth::class)
        subclass(Screen.BookmarksFeed::class)
        subclass(Screen.AddBookmark::class)
        subclass(Screen.Url::class)
        subclass(Screen.Settings::class)
        subclass(Screen.Tags::class)
      }
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
