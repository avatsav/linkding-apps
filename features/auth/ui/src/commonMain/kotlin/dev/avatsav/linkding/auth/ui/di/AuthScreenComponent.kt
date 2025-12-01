package dev.avatsav.linkding.auth.ui.di

import dev.avatsav.linkding.auth.ui.AuthScreen
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.Screen
import dev.avatsav.linkding.navigation.ScreenEntryProviderScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface AuthScreenComponent {
  @IntoSet
  @Provides
  fun provideAuthEntryProviderScope(): ScreenEntryProviderScope = {
    entry<Screen.Auth> { AuthScreen(viewModel = metroViewModel()) }
  }
}
