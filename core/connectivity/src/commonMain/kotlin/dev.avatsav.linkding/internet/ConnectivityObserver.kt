package dev.avatsav.linkding.internet

import co.touchlab.kermit.Logger
import dev.avatsav.linkding.AppCoroutineScope
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

interface ConnectivityObserver {
    val observeIsOnline: StateFlow<Boolean>
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultConnectivityObserver(
    private val networkMonitor: NetworkMonitor,
    appCoroutineScope: AppCoroutineScope,
) : ConnectivityObserver {

    override val observeIsOnline: StateFlow<Boolean> = callbackFlow {
        networkMonitor.setListener { isOnline ->
            Logger.d { "Connectivity changed, isOnline=$isOnline" }
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
