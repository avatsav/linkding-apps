package dev.avatsav.linkding.settings.ui.di

import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.Screen
import dev.avatsav.linkding.navigation.ScreenEntryProviderScope
import dev.avatsav.linkding.settings.ui.SettingsScreen
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface SettingsScreenComponent {
  @IntoSet
  @Provides
  fun provideSettingsEntryProviderScope(): ScreenEntryProviderScope = {
    entry<Screen.Settings> { SettingsScreen(viewModel = metroViewModel()) }
  }
}
