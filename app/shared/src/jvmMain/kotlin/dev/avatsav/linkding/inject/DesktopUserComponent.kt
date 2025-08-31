package dev.avatsav.linkding.inject

import dev.avatsav.linkding.data.model.ApiConfig
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(UserScope::class)
interface DesktopUserComponent : UserComponent {

  @ContributesTo(UiScope::class)
  @GraphExtension.Factory
  interface Factory : UserComponent.Factory {
    override fun create(@Provides apiConfig: ApiConfig): DesktopUserComponent
  }
}
