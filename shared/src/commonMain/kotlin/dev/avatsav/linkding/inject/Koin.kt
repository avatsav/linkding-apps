package dev.avatsav.linkding.inject

import dev.avatsav.linkding.bookmark.adapter.out.LinkdingBookmarkRepository
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.application.ports.out.BookmarkRepository
import dev.avatsav.linkding.bookmark.application.services.LinkdingBookmarkService
import dev.avatsav.linkding.data.ConfigurationStore
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean, appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    if (enableNetworkLogs) Napier.base(DebugAntilog())
    modules(sharedModule(enableNetworkLogs))
}

private val repositoryModule = module {
    single<BookmarkService> { LinkdingBookmarkService(get(), get()) }
    single<BookmarkRepository> { LinkdingBookmarkRepository(get()) }
}

private fun networkModule(enableNetworkLogs: Boolean) = module {
    single { httpClient(enableNetworkLogs) }
}

private val storageModule = module {
    singleOf(::ConfigurationStore)
}

expect fun platformModule(): Module

expect fun viewModelsModule(): Module

fun sharedModule(enableNetworkLogs: Boolean) = listOf(
    networkModule(enableNetworkLogs),
    platformModule(),
    storageModule,
    repositoryModule,
    viewModelsModule(),
)
