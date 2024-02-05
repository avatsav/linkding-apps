package dev.avatsav.linkding.ui.root

import dev.avatsav.linkding.data.model.ApiConfiguration
import dev.avatsav.linkding.domain.observers.ObserveApiConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RootCoordinator(
    @Assisted coroutineScope: CoroutineScope,
    observeApiConfiguration: ObserveApiConfiguration,
) {

    init {
        observeApiConfiguration.invoke(Unit)
    }

    val apiConfiguration = observeApiConfiguration.flow
        .stateIn(coroutineScope, WhileSubscribed(), ApiConfiguration.NotSet)
}
