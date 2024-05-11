package dev.avatsav.linkding.ui.add.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.add.AddBookmarkPresenterFactory
import dev.avatsav.linkding.ui.add.AddBookmarkUiFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface AddBookmarkComponent {

    @IntoSet
    @Provides
    @UiScope
    fun bindSetupPresenterFactory(factory: AddBookmarkPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    @UiScope
    fun bindSetupUiFactoryFactory(factory: AddBookmarkUiFactory): Ui.Factory = factory
}
