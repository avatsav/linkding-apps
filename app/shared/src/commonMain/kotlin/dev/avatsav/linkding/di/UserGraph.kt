package dev.avatsav.linkding.di

import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.zacsweers.metro.SingleIn

@SingleIn(UserScope::class)
interface UserGraph {

  val routeEntryScope: Set<RouteEntryProviderScope>

  val pendingBookmarks: PendingBookmarks

  interface Factory {
    fun create(apiConfig: ApiConfig): UserGraph
  }
}
