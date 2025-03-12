package dev.avatsav.linkding.auth.mappers

import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.data.model.AuthError
import me.tatarka.inject.annotations.Inject

@Inject
class AuthErrorMapper {
  fun map(error: LinkdingError) =
    when (error) {
      is LinkdingError.Connectivity -> AuthError.InvalidHostname(error.message)
      is LinkdingError.Unauthorized -> AuthError.InvalidApiKey(error.message)
      is LinkdingError.Other -> AuthError.Other(error.message)
    }
}
