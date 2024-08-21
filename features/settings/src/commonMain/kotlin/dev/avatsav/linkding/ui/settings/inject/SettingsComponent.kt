package dev.avatsav.linkding.ui.settings.inject

import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.settings.SettingsUiFactory
import dev.avatsav.linkding.ui.settings.SettingsUiPresenterFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface SettingsComponent {

    @IntoSet
    @Provides
    @UserScope
    fun bindSettingsPresenterFactory(factory: SettingsUiPresenterFactory): Presenter.Factory =
        factory

    @IntoSet
    @Provides
    @UserScope
    fun bindSettingsUiFactoryFactory(factory: SettingsUiFactory): Ui.Factory = factory
}
