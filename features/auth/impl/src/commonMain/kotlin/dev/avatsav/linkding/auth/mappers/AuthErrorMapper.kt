package dev.avatsav.linkding.auth.mappers

import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingError.Connectivity
import dev.avatsav.linkding.api.models.LinkdingError.Other
import dev.avatsav.linkding.api.models.LinkdingError.Unauthorized
import dev.avatsav.linkding.data.model.AuthError
import dev.zacsweers.metro.Inject

@Inject
class AuthErrorMapper {
  fun map(error: LinkdingError) =
    when (error) {
      is Connectivity -> AuthError.InvalidHostname(error.message)
      is Unauthorized -> AuthError.InvalidApiKey(error.message)
      is Other -> AuthError.Other(error.message)
    }
}
