package dev.avatsav.linkding.di

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@SingleIn(UserScope::class)
interface UserGraph : ViewModelGraph {
  interface Factory {
    fun create(apiConfig: ApiConfig): UserGraph
  }
}
