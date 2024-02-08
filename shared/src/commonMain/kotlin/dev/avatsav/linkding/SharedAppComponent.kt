package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.LoggerComponent
import dev.avatsav.linkding.prefs.inject.PreferencesComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides

expect interface SharedPlatformAppComponent

interface SharedAppComponent :
    SharedPlatformAppComponent,
    LoggerComponent,
    PreferencesComponent,
    LinkdingConnectionTesterComponent {

    @AppScope
    @Provides
    fun provideCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )
}
