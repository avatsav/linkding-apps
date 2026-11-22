package dev.avatsav.linkding.bookmarks.impl.di

import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksCoordinator
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface PendingBookmarksCoordinatorProviders {
  @Provides
  @SingleIn(AppScope::class)
  fun providePendingBookmarksCoordinator(
    connectivity: ConnectivityObserver
  ): PendingBookmarksCoordinator = PendingBookmarksCoordinator(connectivity)
}
