package dev.avatsav.linkding.navigation

import dev.avatsav.linkding.data.model.Tag
import kotlinx.serialization.Serializable

/**
 * Sealed interface representing all navigable routes in the app.
 *
 * Routes that return results implement [RouteWithResult]. See [Tags] for an example.
 *
 * ```kotlin
 * navigator.goTo(Route.Settings)
 * navigator.goTo(Route.AddBookmark.New)
 * navigator.goTo(Route.AddBookmark.Shared(url = "https://example.com"))
 * navigator.goTo(Route.AddBookmark.Edit(bookmarkId = 123))
 * ```
 */
@Serializable
sealed interface Route {
  /** Key for result routing. Defaults to `toString()`. */
  val key: String
    get() = toString()

  @Serializable data object Auth : Route

  @Serializable data object BookmarksFeed : Route

  @Serializable
  sealed interface AddBookmark : Route {
    @Serializable data object New : AddBookmark

    @Serializable data class Shared(val url: String) : AddBookmark

    @Serializable data class Edit(val bookmarkId: Long) : AddBookmark
  }

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
