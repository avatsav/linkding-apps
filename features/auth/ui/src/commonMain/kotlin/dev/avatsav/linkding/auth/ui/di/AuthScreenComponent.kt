package dev.avatsav.linkding.auth.ui.di

import dev.avatsav.linkding.auth.ui.AuthScreen
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface AuthScreenComponent {
  @IntoSet
  @Provides
  fun provideAuthEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.Auth> { AuthScreen(viewModel = metroViewModel()) }
  }
}
