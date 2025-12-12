package dev.avatsav.linkding.navigation

object TestRoutes {

  val routeA: Route = Route.Auth // key = "Auth"

  val routeB: Route = Route.Settings // key = "Settings"

  val routeC: Route = Route.BookmarksFeed // key = "BookmarksFeed"

  fun routeWithParam(id: Long) =
    Route.AddBookmark(
      sharedUrl = "https://example.com/$id"
    ) // key = "AddBookmark(sharedUrl=https://example.com/$id)"

  fun resultRoute(selectedTagIds: Set<Long> = emptySet()) =
    Route.Tags(selectedTagIds = selectedTagIds) // key = "Tags(selectedTagIds=[...])"

  object Results {
    fun success(tags: List<dev.avatsav.linkding.data.model.Tag>) = Route.Tags.Result.Confirmed(tags)

    val dismissed = Route.Tags.Result.Dismissed
  }
}
