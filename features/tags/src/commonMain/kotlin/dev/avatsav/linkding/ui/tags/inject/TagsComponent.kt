package dev.avatsav.linkding.ui.tags.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.tags.TagsUiFactory
import dev.avatsav.linkding.ui.tags.TagsUiPresenterFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface TagsComponent {

    @IntoSet
    @Provides
    @UserScope
    fun bindTagsPresenterFactory(factory: TagsUiPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    @UserScope
    fun bindTagsUiFactory(factory: TagsUiFactory): Ui.Factory = factory
}
