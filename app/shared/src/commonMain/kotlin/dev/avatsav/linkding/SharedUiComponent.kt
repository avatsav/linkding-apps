package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.add.inject.AddBookmarkComponent
import dev.avatsav.linkding.ui.bookmarks.inject.BookmarksComponent
import dev.avatsav.linkding.ui.root.inject.RootComponent
import dev.avatsav.linkding.ui.settings.inject.SettingsComponent
import dev.avatsav.linkding.ui.setup.inject.SetupComponent
import me.tatarka.inject.annotations.Provides

interface SharedUiComponent :
    RootComponent,
    SetupComponent,
    BookmarksComponent,
    AddBookmarkComponent,
    SettingsComponent {

    @UiScope
    @Provides
    fun provideCircuit(
        uiFactories: Set<Ui.Factory>,
        presenterFactories: Set<Presenter.Factory>,
    ): Circuit = Circuit.Builder()
        .addUiFactories(uiFactories)
        .addPresenterFactories(presenterFactories)
        .build()
}
