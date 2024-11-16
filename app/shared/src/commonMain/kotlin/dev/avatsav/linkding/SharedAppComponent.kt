package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

@ContributesTo(AppScope::class)
interface SharedAppComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppCoroutineScope(
        dispatchers: AppCoroutineDispatchers,
    ): AppCoroutineScope = CoroutineScope(dispatchers.main + SupervisorJob())
}
