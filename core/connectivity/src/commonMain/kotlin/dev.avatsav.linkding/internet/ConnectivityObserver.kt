package dev.avatsav.linkding.internet

import dev.avatsav.linkding.AppCoroutineScope
import dev.avatsav.linkding.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

interface ConnectivityObserver {
    val observeIsOnline: StateFlow<Boolean>
}

@Inject
class DefaultConnectivityObserver(
    private val networkMonitor: NetworkMonitor,
    appCoroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : ConnectivityObserver {

    override val observeIsOnline: StateFlow<Boolean> = callbackFlow {
        networkMonitor.setListener { isOnline ->
            logger.d { "Connectivity changed, isOnline=$isOnline" }
            trySend(isOnline)
        }
        awaitClose {
            networkMonitor.close()
        }
    }.distinctUntilChanged().stateIn(
        scope = appCoroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = networkMonitor.isOnline,
    )
}
