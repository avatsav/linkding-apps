package dev.avatsav.linkding.inject

import dev.avatsav.linkding.data.CredentialsStore
import dev.avatsav.linkding.ui.SetupPresenter
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


/**
 * Presenter dependencies.
 */
private val presenterModule = module {
    factoryOf(::SetupPresenter)
    factory { SetupPresenter(get()) }
}

/**
 * Repository dependencies.
 */
private val repositoryModule = module {

}

/**
 * Network dependencies.
 */
private val networkModule = module {

}

/**
 * Storage/DB/Caching dependencies.
 */
private val storageModule = module {
    //
    singleOf(::CredentialsStore)
}


val sharedModule = listOf(
    networkModule,
    storageModule,
    repositoryModule,
    presenterModule
)
