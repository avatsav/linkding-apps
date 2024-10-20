package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.Named
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.UserScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesSubcomponent(UserScope::class)
@SingleIn(UserScope::class)
interface UserComponent {

    @Provides
    @SingleIn(UserScope::class)
    @Named(CircuitInstance.AUTHENTICATED)
    fun provideAuthenticatedCircuit(
        uiFactories: Set<Ui.Factory>,
        presenterFactories: Set<Presenter.Factory>,
    ): Circuit =
        Circuit.Builder()
            .addUiFactories(uiFactories)
            .addPresenterFactories(presenterFactories)
            .build()

    @ContributesSubcomponent.Factory(UiScope::class)
    interface Factory {
        fun createUiComponent(apiConfig: ApiConfig): UserComponent
    }
}
