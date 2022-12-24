package dev.avatsav.linkding.inject

import com.russhwolf.settings.ExperimentalSettingsApi
import dev.avatsav.linkding.bookmark.adapter.out.LinkdingBookmarkRepository
import dev.avatsav.linkding.bookmark.application.services.LinkdingBookmarkService
import dev.avatsav.linkding.data.CredentialsStore
import dev.avatsav.linkding.ui.MainPresenter
import dev.avatsav.linkding.ui.SetupPresenter
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean, appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModule(enableNetworkLogs))
}

/**
 * Presenter dependencies.
 */
private val presenterModule = module {
    factoryOf(::SetupPresenter)
    factoryOf(::MainPresenter)
}

/**
 * Repository dependencies.
 */
private val repositoryModule = module {
    single { LinkdingBookmarkService(get(), get()) }
    single { LinkdingBookmarkRepository(get()) }
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
@OptIn(ExperimentalSettingsApi::class)
private val storageModule = module {
    singleOf(::CredentialsStore)
}

expect fun platformModule(): Module

fun sharedModule(enableNetworkLogs: Boolean) = listOf(
    networkModule(enableNetworkLogs),
    platformModule(),
    storageModule,
    repositoryModule,
    presenterModule,
)
