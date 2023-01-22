package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.bookmarks.AddBookmarkViewModel
import dev.avatsav.linkding.ui.bookmarks.BookmarksViewModel
import dev.avatsav.linkding.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual fun viewModelsModule() = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { BookmarksViewModel(get()) }
    viewModel { AddBookmarkViewModel(get(), get()) }
}