package dev.avatsav.linkding.data.model

data class BookmarkError(val message: String)

sealed class AuthError(open val message: String) {
  data class InvalidHostname(override val message: String) : AuthError(message)

  data class InvalidApiKey(override val message: String) : AuthError(message)

  data class Other(override val message: String) : AuthError(message)
}
