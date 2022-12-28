package dev.avatsav.linkding.ui

import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.data.ConfigurationNotSetup
import dev.avatsav.linkding.data.ConfigurationStore
import dev.avatsav.linkding.data.Setup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MainPresenter(configurationStore: ConfigurationStore) : Presenter() {
    data class ViewState(val loading: Boolean = false, val setup: Boolean)

    val state: StateFlow<ViewState> = configurationStore.state.map { state ->
        when (state) {
            is ConfigurationNotSetup -> ViewState(loading = false, setup = false)
            is Setup -> ViewState(loading = false, setup = true)
        }
    }.onEach { delay(300) }.stateIn(
        scope = presenterScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ViewState(loading = true, setup = false)
    )

}

