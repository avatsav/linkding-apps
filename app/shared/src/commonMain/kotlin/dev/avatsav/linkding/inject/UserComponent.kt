package dev.avatsav.linkding.inject

import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.qualifier.Authenticated
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(UserScope::class)
interface UserComponent {

  @Authenticated val circuit: Circuit

  @ContributesTo(UiScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(@Provides apiConfig: ApiConfig): UserComponent
  }
}
