package dev.avatsav.linkding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface SharedAppComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.IO,
      computation = Dispatchers.Default,
      main = Dispatchers.Main,
    )

  @Provides
  @SingleIn(AppScope::class)
  fun provideAppCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    CoroutineScope(dispatchers.main + SupervisorJob())
}
