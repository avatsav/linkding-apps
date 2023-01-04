package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.presenter.AddBookmarkPresenter
import dev.avatsav.linkding.ui.presenter.BookmarksPresenter
import dev.avatsav.linkding.ui.presenter.HomePresenter
import org.koin.core.Koin

class IosDependencies(koin: Koin) {
    val homePresenter: HomePresenter = koin.get()
    val bookmarkPresenter: BookmarksPresenter = koin.get()
    val addBookmarkPresenter: AddBookmarkPresenter = koin.get()
}