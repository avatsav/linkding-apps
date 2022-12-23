package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.MainPresenter
import dev.avatsav.linkding.ui.SetupPresenter
import org.koin.core.Koin

class IosDependencies(private val koin: Koin) {
    val setupPresenter: SetupPresenter = koin.get()
    val mainPresenter: MainPresenter = koin.get()
}