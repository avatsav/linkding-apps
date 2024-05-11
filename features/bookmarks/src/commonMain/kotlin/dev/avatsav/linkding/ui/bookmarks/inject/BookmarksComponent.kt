package dev.avatsav.linkding.ui.bookmarks.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiFactory
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiPresenterFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface BookmarksComponent {

    @IntoSet
    @Provides
    @UiScope
    fun bindBookmarksPresenterFactory(factory: BookmarksUiPresenterFactory): Presenter.Factory =
        factory

    @IntoSet
    @Provides
    @UiScope
    fun bindBookmarksUiFactoryFactory(factory: BookmarksUiFactory): Ui.Factory = factory
}
