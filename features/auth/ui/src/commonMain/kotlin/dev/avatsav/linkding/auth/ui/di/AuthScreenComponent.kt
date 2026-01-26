package dev.avatsav.linkding.auth.ui.di

import dev.avatsav.linkding.auth.ui.AuthPresenter
import dev.avatsav.linkding.auth.ui.AuthScreen
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.avatsav.linkding.viewmodel.retainedPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(UiScope::class)
interface AuthScreenComponent {
  @IntoSet
  @Provides
  fun provideAuthEntryProviderScope(authPresenter: AuthPresenter): RouteEntryProviderScope = {
    entry<Route.Auth> { AuthScreen(presenter = retainedPresenter(authPresenter)) }
  }
}
