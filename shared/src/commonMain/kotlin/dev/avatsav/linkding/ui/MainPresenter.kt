package dev.avatsav.linkding.ui

import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.data.CredentialsNotSetup
import dev.avatsav.linkding.data.CredentialsStore
import dev.avatsav.linkding.data.Setup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainPresenter(private val credentialsStore: CredentialsStore) : Presenter() {

    val credentialsSetup: StateFlow<Boolean> = credentialsStore.credentialsState.map { state ->
        when (state) {
            CredentialsNotSetup -> false
            is Setup -> true
        }
    }.stateIn(scope = presenterScope, SharingStarted.Lazily, false)

}

