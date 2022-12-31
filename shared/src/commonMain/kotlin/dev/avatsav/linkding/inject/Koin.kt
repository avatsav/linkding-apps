package dev.avatsav.linkding.inject

import dev.avatsav.linkding.bookmark.adapter.out.LinkdingBookmarkRepository
import dev.avatsav.linkding.bookmark.application.services.LinkdingBookmarkService
import dev.avatsav.linkding.data.ConfigurationStore
import dev.avatsav.linkding.ui.AddBookmarkPresenter
import dev.avatsav.linkding.ui.BookmarksPresenter
import dev.avatsav.linkding.ui.MainPresenter
import dev.avatsav.linkding.ui.SetupConfigurationPresenter
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean, appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    if (enableNetworkLogs) Napier.base(DebugAntilog())
    modules(sharedModule(enableNetworkLogs))
}

/**
 * Presenter dependencies.
 */
private val presenterModule = module {
    factoryOf(::SetupConfigurationPresenter)
    factoryOf(::MainPresenter)
    factoryOf(::BookmarksPresenter)
    factoryOf(::AddBookmarkPresenter)
}

/**
 * Repository dependencies.
 */
private val repositoryModule = module {
    singleOf(::LinkdingBookmarkService)
    singleOf(::LinkdingBookmarkRepository)
}

/**
 * Network dependencies.
 */
private fun networkModule(enableNetworkLogs: Boolean) = module {
    single { httpClient(enableNetworkLogs) }
}

/**
 * Storage/DB/Caching dependencies.
 */
private val storageModule = module {
    singleOf(::ConfigurationStore)
}

expect fun platformModule(): Module

fun sharedModule(enableNetworkLogs: Boolean) = listOf(
    networkModule(enableNetworkLogs),
    platformModule(),
    storageModule,
    repositoryModule,
    presenterModule,
)
