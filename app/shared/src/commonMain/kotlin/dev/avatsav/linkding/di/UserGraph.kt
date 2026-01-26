package dev.avatsav.linkding.di

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.zacsweers.metro.SingleIn

@SingleIn(UserScope::class)
interface UserGraph {

  val routeEntryScope: Set<RouteEntryProviderScope>

  interface Factory {
    fun create(apiConfig: ApiConfig): UserGraph
  }
}
