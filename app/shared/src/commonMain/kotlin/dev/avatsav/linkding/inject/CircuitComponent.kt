package dev.avatsav.linkding.inject

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.qualifier.Authenticated
import dev.avatsav.linkding.inject.qualifier.Unauthenticated
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(UserScope::class)
interface AuthenticatedCircuitComponent {

  @Provides
  @SingleIn(UserScope::class)
  @Authenticated
  fun provideCircuit(
    uiFactories: Set<Ui.Factory>,
    presenterFactories: Set<Presenter.Factory>,
  ): Circuit =
    Circuit.Builder().addUiFactories(uiFactories).addPresenterFactories(presenterFactories).build()
}

@ContributesTo(UiScope::class)
interface UnauthenticatedCircuitComponent {

  @Provides
  @SingleIn(UiScope::class)
  @Unauthenticated
  fun provideCircuit(
    uiFactories: Set<Ui.Factory>,
    presenterFactories: Set<Presenter.Factory>,
  ): Circuit =
    Circuit.Builder().addUiFactories(uiFactories).addPresenterFactories(presenterFactories).build()
}
