package dev.avatsav.linkding.settings.ui.di

import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.avatsav.linkding.settings.ui.SettingsScreen
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface SettingsScreenComponent {
  @IntoSet
  @Provides
  fun provideSettingsEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.Settings> { SettingsScreen(viewModel = metroViewModel()) }
  }
}
