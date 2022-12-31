package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.AddBookmarkPresenter
import dev.avatsav.linkding.ui.MainPresenter
import dev.avatsav.linkding.ui.SetupConfigurationPresenter
import org.koin.core.Koin

class IosDependencies(koin: Koin) {
    val setupConfigurationPresenter: SetupConfigurationPresenter = koin.get()
    val mainPresenter: MainPresenter = koin.get()
    val addBookmarkPresenter: AddBookmarkPresenter = koin.get()
}