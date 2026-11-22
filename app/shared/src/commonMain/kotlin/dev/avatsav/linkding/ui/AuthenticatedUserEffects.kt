package dev.avatsav.linkding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks

/** The authenticated root owns foreground execution; user graphs only provide dependencies. */
@Composable
internal fun AuthenticatedUserEffects(pendingBookmarks: PendingBookmarks) {
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  LaunchedEffect(pendingBookmarks, lifecycle) {
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { pendingBookmarks.runUploader() }
  }
}
