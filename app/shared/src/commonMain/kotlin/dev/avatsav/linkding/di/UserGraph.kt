package dev.avatsav.linkding.di

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.zacsweers.metro.SingleIn

@SingleIn(UserScope::class)
interface UserGraph {
  interface Factory {
    fun create(apiConfig: ApiConfig): UserGraph
  }
}
