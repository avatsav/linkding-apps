package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.data.bookmarks.inject.LinkdingComponent
import dev.avatsav.linkding.inject.Named
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.AuthenticatedAppUi
import dev.avatsav.linkding.ui.DefaultAuthenticatedAppUi
import dev.avatsav.linkding.ui.add.inject.AddBookmarkComponent
import dev.avatsav.linkding.ui.bookmarks.inject.BookmarksComponent
import dev.avatsav.linkding.ui.settings.inject.SettingsComponent
import dev.avatsav.linkding.ui.tags.inject.TagsComponent
import me.tatarka.inject.annotations.Provides

interface SharedUserComponent :
    LinkdingComponent,
    BookmarksComponent,
    AddBookmarkComponent,
    SettingsComponent,
    TagsComponent {

    abstract val authenticatedAppUi: AuthenticatedAppUi

    @UserScope
    val DefaultAuthenticatedAppUi.bind: AuthenticatedAppUi
        @Provides get() = this

    @Provides
    @UserScope
    fun provideAuthenticatedCircuit(
        uiFactories: Set<Ui.Factory>,
        presenterFactories: Set<Presenter.Factory>,
    ):
        @Named(CircuitInstance.AUTHENTICATED)
        Circuit =
        Circuit.Builder()
            .addUiFactories(uiFactories)
            .addPresenterFactories(presenterFactories)
            .build()
}
