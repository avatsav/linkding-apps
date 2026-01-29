package dev.avatsav.linkding.settings.ui.di

import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.avatsav.linkding.presenter.retainPresenter
import dev.avatsav.linkding.settings.ui.SettingsPresenter
import dev.avatsav.linkding.settings.ui.SettingsScreen
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides

@ContributesTo(UserScope::class)
interface SettingsScreenProviders {
  @IntoSet
  @Provides
  fun provideSettingsEntryProviderScope(
    presenter: Provider<SettingsPresenter>
  ): RouteEntryProviderScope = {
    entry<Route.Settings> { SettingsScreen(retainPresenter { presenter() }) }
  }
}
