package dev.avatsav.linkding.ui.setup.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.setup.SetupUiFactory
import dev.avatsav.linkding.ui.setup.SetupUiPresenterFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface SetupComponent {

    @IntoSet
    @Provides
    @UiScope
    fun bindSetupPresenterFactory(factory: SetupUiPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    @UiScope
    fun bindSetupUiFactoryFactory(factory: SetupUiFactory): Ui.Factory = factory
}
