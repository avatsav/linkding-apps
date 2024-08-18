package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.data.bookmarks.inject.LinkdingComponent
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.Named
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.AuthenticatedContent
import dev.avatsav.linkding.ui.add.inject.AddBookmarkComponent
import dev.avatsav.linkding.ui.bookmarks.inject.BookmarksComponent
import dev.avatsav.linkding.ui.settings.inject.SettingsComponent
import dev.avatsav.linkding.ui.tags.inject.TagsComponent
import me.tatarka.inject.annotations.Provides

typealias UserComponentFactory = (ApiConfig) -> SharedUserComponent

interface SharedUserComponent :
    LinkdingComponent,
    BookmarksComponent,
    AddBookmarkComponent,
    SettingsComponent,
    TagsComponent {

    abstract val authenticatedContent: AuthenticatedContent

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
