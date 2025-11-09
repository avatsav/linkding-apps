package dev.avatsav.linkding.inject

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.di.scope.UiScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(UiScope::class)
interface CircuitComponent {

  @Provides
  @SingleIn(UiScope::class)
  fun provideCircuit(
    uiFactories: Set<Ui.Factory>,
    presenterFactories: Set<Presenter.Factory>,
  ): Circuit =
    Circuit.Builder().addUiFactories(uiFactories).addPresenterFactories(presenterFactories).build()
}
