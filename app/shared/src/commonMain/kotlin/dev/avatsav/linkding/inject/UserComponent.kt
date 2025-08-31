package dev.avatsav.linkding.inject

import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.qualifier.Authenticated
import dev.zacsweers.metro.SingleIn

@SingleIn(UserScope::class)
interface UserComponent {

  @Authenticated val circuit: Circuit

  interface Factory {
    fun create(apiConfig: ApiConfig): UserComponent
  }
}
