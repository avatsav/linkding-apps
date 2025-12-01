package dev.avatsav.linkding.bookmarks.ui.di

import dev.avatsav.linkding.bookmarks.ui.list.BookmarksScreen
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.Screen
import dev.avatsav.linkding.navigation.ScreenEntryProviderScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface BookmarksScreenComponent {
  @IntoSet
  @Provides
  fun provideBookmarksEntryProviderScope(): ScreenEntryProviderScope = {
    entry<Screen.BookmarksFeed> { BookmarksScreen(viewModel = metroViewModel()) }
  }
}
