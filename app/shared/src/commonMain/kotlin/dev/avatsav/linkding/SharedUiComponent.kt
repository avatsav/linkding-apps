package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.Named
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.setup.inject.SetupComponent
import me.tatarka.inject.annotations.Provides

interface SharedUiComponent : SetupComponent {

    @UiScope
    @Provides
    fun provideCircuit(
        uiFactories: Set<Ui.Factory>,
        presenterFactories: Set<Presenter.Factory>,
    ):
        @Named(CircuitInstance.UNAUTHENTICATED)
        Circuit = Circuit.Builder()
        .addUiFactories(uiFactories)
        .addPresenterFactories(presenterFactories)
        .build()
}
