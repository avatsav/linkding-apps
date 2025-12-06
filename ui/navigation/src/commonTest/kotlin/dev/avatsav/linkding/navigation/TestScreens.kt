package dev.avatsav.linkding.navigation

/**
 * Test screen aliases using the actual Screen types.
 *
 * Screen keys now default to `toString()`, providing predictable, content-based keys for testing.
 */
object TestScreens {

  val screenA: Screen = Screen.Auth // key = "Auth"

  val screenB: Screen = Screen.Settings // key = "Settings"

  val screenC: Screen = Screen.BookmarksFeed // key = "BookmarksFeed"

  fun screenWithParam(id: Long) =
    Screen.AddBookmark(
      sharedUrl = "https://example.com/$id"
    ) // key = "AddBookmark(sharedUrl=https://example.com/$id)"

  /** Use the existing Tags screen which already implements ScreenWithResult. */
  fun resultScreen(selectedTagIds: List<Long> = emptyList()) =
    Screen.Tags(selectedTagIds = selectedTagIds) // key = "Tags(selectedTagIds=[...])"

  /** Result type aliases for Tags screen results. */
  object Results {
    fun success(tags: List<dev.avatsav.linkding.data.model.Tag>) =
      Screen.Tags.Result.Confirmed(tags)

    val dismissed = Screen.Tags.Result.Dismissed
  }
}
