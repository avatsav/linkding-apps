package dev.avatsav.linkding.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test

class AuthenticatedUserEffectsTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun `uploads follow foreground lifecycle account replacement and authentication removal`() {
    val owner =
      object : LifecycleOwner {
        override val lifecycle = LifecycleRegistry.createUnsafe(this)
      }
    owner.lifecycle.currentState = Lifecycle.State.CREATED
    val first = RecordingPendingBookmarks()
    val second = RecordingPendingBookmarks()
    var activeAccount by mutableStateOf<PendingBookmarks?>(first)
    compose.setContent {
      CompositionLocalProvider(LocalLifecycleOwner provides owner) {
        activeAccount?.let { AuthenticatedUserEffects(it) }
      }
    }
    compose.runOnIdle { assertEquals(0, first.starts.get()) }
    compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.STARTED }
    compose.waitUntil { first.starts.get() == 1 }
    compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.CREATED }
    compose.waitUntil { first.stops.get() == 1 }
    compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.STARTED }
    compose.waitUntil { first.starts.get() == 2 }
    compose.runOnIdle { activeAccount = second }
    compose.waitUntil { first.stops.get() == 2 && second.starts.get() == 1 }
    compose.runOnIdle { activeAccount = null }
    compose.waitUntil { second.stops.get() == 1 }
  }

  private class RecordingPendingBookmarks : PendingBookmarks {
    val starts = AtomicInteger()
    val stops = AtomicInteger()

    override fun observe() = emptyFlow<List<PendingBookmark>>()

    override suspend fun get(id: Long) = error("Unexpected read")

    override suspend fun create(bookmark: SaveBookmark) = error("Unexpected creation")

    override suspend fun edit(id: Long, bookmark: SaveBookmark) = error("Unexpected edit")

    override suspend fun discard(id: Long) = error("Unexpected discard")

    override suspend fun retry(id: Long?) = error("Unexpected retry")

    override suspend fun overwrite(id: Long) = error("Unexpected overwrite")

    override suspend fun resetAccountIfEmpty() = error("Unexpected reset")

    override suspend fun runUploader() {
      starts.incrementAndGet()
      try {
        awaitCancellation()
      } finally {
        stops.incrementAndGet()
      }
    }
  }
}
