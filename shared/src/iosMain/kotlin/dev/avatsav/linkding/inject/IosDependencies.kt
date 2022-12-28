package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.MainPresenter
import dev.avatsav.linkding.ui.SetupConfigurationPresenter
import org.koin.core.Koin

class IosDependencies(private val koin: Koin) {
    val setupConfigurationPresenter: SetupConfigurationPresenter = koin.get()
    val mainPresenter: MainPresenter = koin.get()
}