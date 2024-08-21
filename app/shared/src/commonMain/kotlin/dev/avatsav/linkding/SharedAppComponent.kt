package dev.avatsav.linkding

import dev.avatsav.linkding.data.auth.inject.AuthComponent
import dev.avatsav.linkding.data.db.inject.DatabaseComponent
import dev.avatsav.linkding.data.network.inject.NetworkComponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.LoggerComponent
import dev.avatsav.linkding.internet.inject.ConnectivityComponent
import dev.avatsav.linkding.prefs.inject.PreferencesComponent
import me.tatarka.inject.annotations.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

expect interface SharedPlatformAppComponent

interface SharedAppComponent :
    SharedPlatformAppComponent,
    NetworkComponent,
    LoggerComponent,
    PreferencesComponent,
    ConnectivityComponent,
    AuthComponent,
    DatabaseComponent {

    @AppScope
    @Provides
    fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )

    @AppScope
    @Provides
    fun provideAppCoroutineScope(
        dispatchers: AppCoroutineDispatchers,
    ): AppCoroutineScope = CoroutineScope(dispatchers.main + SupervisorJob())
}
