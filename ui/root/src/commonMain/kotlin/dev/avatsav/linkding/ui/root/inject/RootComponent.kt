package dev.avatsav.linkding.ui.root.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.root.AppUi
import dev.avatsav.linkding.ui.root.DefaultAppUi
import dev.avatsav.linkding.ui.root.RootUiFactory
import dev.avatsav.linkding.ui.root.RootUiPresenterFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface RootComponent {

    @IntoSet
    @Provides
    @UiScope
    fun bindRootPresenterFactory(factory: RootUiPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    @UiScope
    fun bindRootUiFactoryFactory(factory: RootUiFactory): Ui.Factory = factory

    @Provides
    @UiScope
    fun bindAppUi(bind: DefaultAppUi): AppUi = bind
}
