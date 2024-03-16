package dev.avatsav.linkding

import dev.avatsav.linkding.api.LinkdingApiComponent
import dev.avatsav.linkding.data.db.inject.DatabaseComponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.LoggerComponent
import dev.avatsav.linkding.internet.inject.ConnectivityComponent
import dev.avatsav.linkding.prefs.inject.PreferencesComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Provides

expect interface SharedPlatformAppComponent

interface SharedAppComponent :
    SharedPlatformAppComponent,
    LoggerComponent,
    PreferencesComponent,
    ConnectivityComponent,
    LinkdingApiComponent,
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
