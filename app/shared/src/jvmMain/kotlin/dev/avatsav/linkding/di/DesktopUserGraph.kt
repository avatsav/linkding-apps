package dev.avatsav.linkding.di

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(UserScope::class)
interface DesktopUserGraph : UserGraph {

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory : UserGraph.Factory {
    override fun create(@Provides apiConfig: ApiConfig): DesktopUserGraph
  }
}
