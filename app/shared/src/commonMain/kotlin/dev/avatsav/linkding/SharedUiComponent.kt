package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.Named
import dev.avatsav.linkding.inject.UiScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(UiScope::class)
interface SharedUiComponent {

    @Provides
    @SingleIn(UiScope::class)
    @Named(CircuitInstance.UNAUTHENTICATED)
    fun provideCircuit(
        uiFactories: Set<Ui.Factory>,
        presenterFactories: Set<Presenter.Factory>,
    ): Circuit = Circuit.Builder()
//        .addUiFactories(uiFactories)
//        .addPresenterFactories(presenterFactories)
        .build()
}
