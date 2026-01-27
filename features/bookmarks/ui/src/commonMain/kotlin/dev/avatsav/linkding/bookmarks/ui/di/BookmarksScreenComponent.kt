package dev.avatsav.linkding.bookmarks.ui.di

import androidx.compose.material3.ExperimentalMaterial3Api
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkPresenter
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkScreen
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksPresenter
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksScreen
import dev.avatsav.linkding.bookmarks.ui.tags.TagsPresenter
import dev.avatsav.linkding.bookmarks.ui.tags.TagsScreen
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.navigation.BottomSheetSceneStrategy
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.avatsav.linkding.presenter.retainedPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides

@ContributesTo(UserScope::class)
interface BookmarksScreenComponent {
  @IntoSet
  @Provides
  fun provideBookmarksEntryProviderScope(
    presenter: Provider<BookmarksPresenter>
  ): RouteEntryProviderScope = {
    entry<Route.BookmarksFeed> { BookmarksScreen(retainedPresenter(presenter())) }
  }

  @IntoSet
  @Provides
  fun provideNewBookmarkEntryProviderScope(
    factory: AddBookmarkPresenter.Factory
  ): RouteEntryProviderScope = {
    entry<Route.AddBookmark.New> { AddBookmarkScreen(retainedPresenter(factory.create(it))) }
  }

  @IntoSet
  @Provides
  fun provideSharedBookmarkEntryProviderScope(
    factory: AddBookmarkPresenter.Factory
  ): RouteEntryProviderScope = {
    entry<Route.AddBookmark.Shared> { AddBookmarkScreen(retainedPresenter(factory.create(it))) }
  }

  @IntoSet
  @Provides
  fun provideEditBookmarkEntryProviderScope(
    factory: AddBookmarkPresenter.Factory
  ): RouteEntryProviderScope = {
    entry<Route.AddBookmark.Edit> { AddBookmarkScreen(retainedPresenter(factory.create(it))) }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @IntoSet
  @Provides
  fun provideTagsEntryProviderScope(factory: TagsPresenter.Factory): RouteEntryProviderScope = {
    entry<Route.Tags>(metadata = BottomSheetSceneStrategy.bottomSheetExpanded()) {
      TagsScreen(retainedPresenter(factory.create(it.selectedTagIds)))
    }
  }
}
