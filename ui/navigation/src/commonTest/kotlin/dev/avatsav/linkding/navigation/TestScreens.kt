package dev.avatsav.linkding.navigation

/**
 * Test screen aliases using the actual Screen types.
 *
 * Since Screen is a sealed interface, we use factory functions to create test screen instances with
 * predictable keys for testing.
 */
object TestScreens {

  fun screenA(key: String = "screen-a") = Screen.Auth(key)

  fun screenB(key: String = "screen-b") = Screen.Settings(key)

  fun screenC(key: String = "screen-c") = Screen.BookmarksFeed(key)

  fun screenWithParam(id: Long) =
    Screen.AddBookmark(sharedUrl = "https://example.com/$id", key = "screen-with-param-$id")

  /** Use the existing Tags screen which already implements ScreenWithResult. */
  fun resultScreen(key: String = "result-screen") = Screen.Tags(key = key)

  /** Result type aliases for Tags screen results. */
  object Results {
    fun success(tags: List<dev.avatsav.linkding.data.model.Tag>) =
      Screen.Tags.Result.Confirmed(tags)

    val dismissed = Screen.Tags.Result.Dismissed
  }
}
