package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.bookmarks.AddBookmarkViewModel
import dev.avatsav.linkding.ui.bookmarks.BookmarksViewModel
import dev.avatsav.linkding.ui.home.HomeViewModel
import org.koin.core.Koin
import org.koin.dsl.module

actual fun viewModelsModule() = module {
    factory { HomeViewModel(get(), get()) }
    factory { BookmarksViewModel(get()) }
    factory { AddBookmarkViewModel(get(), get()) }
}

/**
 * This class is used in iOS to make the ViewModel available.
 */
class ViewModelsContainer(koin: Koin) {
    val homeViewModel: HomeViewModel = koin.get()
    val bookmarksViewModel: BookmarksViewModel = koin.get()
    val addBookmarkViewModel: AddBookmarkViewModel = koin.get()
}