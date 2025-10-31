package dev.avatsav.linkding.bookmarks.ui.list.common

data class SnackbarMessage(
  val message: String,
  val actionLabel: String? = null,
  val onAction: (() -> Unit)? = null,
)
