package dev.avatsav.linkding.data.model.app

sealed interface LaunchMode {
  object Normal : LaunchMode

  data class SharedLink(val sharedLink: String) : LaunchMode
}
