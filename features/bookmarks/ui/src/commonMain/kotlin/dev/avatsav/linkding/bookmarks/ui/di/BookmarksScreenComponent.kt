package dev.avatsav.linkding.bookmarks.ui.di

import androidx.compose.material3.ExperimentalMaterial3Api
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkScreen
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkViewModel
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksScreen
import dev.avatsav.linkding.bookmarks.ui.tags.TagsScreen
import dev.avatsav.linkding.bookmarks.ui.tags.TagsViewModel
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.BottomSheetSceneStrategy
import dev.avatsav.linkding.navigation.Screen
import dev.avatsav.linkding.navigation.ScreenEntryProviderScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel

@ContributesTo(UiScope::class)
interface BookmarksScreenComponent {
  @IntoSet
  @Provides
  fun provideBookmarksEntryProviderScope(): ScreenEntryProviderScope = {
    entry<Screen.BookmarksFeed> { BookmarksScreen(viewModel = metroViewModel()) }
  }

  @IntoSet
  @Provides
  fun provideAddBookmarkEntryProviderScope(): ScreenEntryProviderScope = {
    entry<Screen.AddBookmark> { screen ->
      AddBookmarkScreen(
        viewModel =
          assistedMetroViewModel<AddBookmarkViewModel, AddBookmarkViewModel.Factory> {
            create(screen.sharedUrl)
          }
      )
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @IntoSet
  @Provides
  fun provideTagsEntryProviderScope(): ScreenEntryProviderScope = {
    entry<Screen.Tags>(metadata = BottomSheetSceneStrategy.bottomSheetExpanded()) { screen ->
      TagsScreen(
        viewModel =
          assistedMetroViewModel<TagsViewModel, TagsViewModel.Factory> {
            create(screen.selectedTagIds)
          }
      )
    }
  }
}
