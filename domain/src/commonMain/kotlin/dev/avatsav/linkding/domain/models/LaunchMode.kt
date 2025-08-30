package dev.avatsav.linkding.domain.models

sealed interface LaunchMode {
  object Normal : LaunchMode

  data class SharedLink(val sharedLink: String) : LaunchMode
}
