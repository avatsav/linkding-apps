package dev.avatsav.linkding.bookmarks.impl

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Shares upload ownership across activity/user graphs without owning any coroutines. */
class PendingBookmarksCoordinator {
  private val mutex = Mutex()
  private val accounts = mutableMapOf<String, Account>()

  internal suspend fun account(id: String): Account = mutex.withLock {
    accounts.getOrPut(id) { Account() }
  }

  internal class Account {
    // Local writes never wait for network work. Only the foreground leader holds uploaderMutex.
    val localMutex = Mutex()
    val uploaderMutex = Mutex()
    val wakeups = Channel<Unit>(Channel.CONFLATED)
    // Protected by localMutex. Requests survive collector gaps, but not process death.
    val retryIds = mutableSetOf<Long>()
  }
}
