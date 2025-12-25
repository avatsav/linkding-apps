package dev.avatsav.linkding.bookmarks.ui.di

import androidx.compose.material3.ExperimentalMaterial3Api
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkScreen
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkViewModel
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksScreen
import dev.avatsav.linkding.bookmarks.ui.tags.TagsScreen
import dev.avatsav.linkding.bookmarks.ui.tags.TagsViewModel
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.BottomSheetSceneStrategy
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface BookmarksScreenComponent {
  @IntoSet
  @Provides
  fun provideBookmarksEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.BookmarksFeed> { BookmarksScreen(viewModel = metroViewModel()) }
  }

  @IntoSet
  @Provides
  fun provideNewBookmarkEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.AddBookmark.New> {
      AddBookmarkScreen(
        viewModel =
          assistedMetroViewModel<AddBookmarkViewModel, AddBookmarkViewModel.Factory> {
            create(Route.AddBookmark.New)
          }
      )
    }
  }

  @IntoSet
  @Provides
  fun provideSharedBookmarkEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.AddBookmark.Shared> { route ->
      AddBookmarkScreen(
        viewModel =
          assistedMetroViewModel<AddBookmarkViewModel, AddBookmarkViewModel.Factory> {
            create(route)
          }
      )
    }
  }

  @IntoSet
  @Provides
  fun provideEditBookmarkEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.AddBookmark.Edit> { route ->
      AddBookmarkScreen(
        viewModel =
          assistedMetroViewModel<AddBookmarkViewModel, AddBookmarkViewModel.Factory> {
            create(route)
          }
      )
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @IntoSet
  @Provides
  fun provideTagsEntryProviderScope(): RouteEntryProviderScope = {
    entry<Route.Tags>(metadata = BottomSheetSceneStrategy.bottomSheetExpanded()) { route ->
      TagsScreen(
        viewModel =
          assistedMetroViewModel<TagsViewModel, TagsViewModel.Factory> {
            create(route.selectedTagIds)
          }
      )
    }
  }
}
