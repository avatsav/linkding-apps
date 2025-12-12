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
 * Sealed interface representing all navigable routes in the app.
 *
 * Routes that return results implement [RouteWithResult]. See [Tags] for an example.
 *
 * ```kotlin
 * navigator.goTo(Route.Settings)
 * navigator.goTo(Route.AddBookmark(sharedUrl = "https://example.com"))
 * ```
 */
@Serializable
sealed interface Route {
  /** Key for result routing. Defaults to `toString()`. */
  val key: String
    get() = toString()

  @Serializable data object Auth : Route

  @Serializable data object BookmarksFeed : Route

  @Serializable data class AddBookmark(val sharedUrl: String? = null) : Route

  @Serializable data class Url(val url: String) : Route

  @Serializable data object Settings : Route

  @Serializable
  data class Tags(val selectedTagIds: Set<Long> = emptySet()) :
    Route, RouteWithResult<Tags.Result> {

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
      polymorphic(Route::class) {
        subclass(Route.Auth::class)
        subclass(Route.BookmarksFeed::class)
        subclass(Route.AddBookmark::class)
        subclass(Route.Url::class)
        subclass(Route.Settings::class)
        subclass(Route.Tags::class)
      }
    }
  }
}

/**
 * Marker interface for routes that return a typed result.
 *
 * ```kotlin
 * data class Tags(...) : Route, RouteWithResult<Tags.Result> {
 *   sealed interface Result : NavResult { ... }
 * }
 * ```
 */
interface RouteWithResult<R : NavResult>
