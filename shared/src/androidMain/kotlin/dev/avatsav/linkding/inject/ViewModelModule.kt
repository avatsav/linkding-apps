package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.viewmodel.AddBookmarkViewModel
import dev.avatsav.linkding.ui.viewmodel.BookmarksViewModel
import dev.avatsav.linkding.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual fun viewModelsModule() = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { BookmarksViewModel(get()) }
    viewModel { AddBookmarkViewModel(get(), get()) }
}