package dev.avatsav.linkding.bookmarks.impl

import dev.avatsav.linkding.internet.ConnectivityObserver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Shares upload ownership across activity/user graphs without owning any coroutines. */
class PendingBookmarksCoordinator(connectivity: ConnectivityObserver) {
  internal val connectivityWakeups = connectivity.observeIsOnline.filter { it }.map { Unit }
  private val mutex = Mutex()
  private val accounts = mutableMapOf<String, Account>()

  internal suspend fun account(id: String): Account = mutex.withLock {
    accounts.getOrPut(id) { Account() }
  }

  internal suspend fun <T> locally(id: String, block: suspend Account.() -> T): T {
    val account = account(id)
    return account.localMutex.withLock { account.block() }
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
