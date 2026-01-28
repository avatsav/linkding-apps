package dev.avatsav.linkding.di

import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.AppCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

@ContributesTo(AppScope::class)
interface CoroutinesProviders {

  @Provides
  @SingleIn(AppScope::class)
  @Suppress("InjectDispatcher")
  fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.IO,
      default = Dispatchers.Default,
      main = Dispatchers.Main,
    )

  @Provides
  @SingleIn(AppScope::class)
  fun provideAppCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    CoroutineScope(dispatchers.main + SupervisorJob())
}
