package dev.avatsav.linkding.data.db.di

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.DatabaseFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

expect interface SqlDelightPlatformProviders

@ContributesTo(AppScope::class)
interface SqlDelightProviders : SqlDelightPlatformProviders {
  @SingleIn(AppScope::class)
  @Provides
  fun provideSqlDelightDatabase(factory: DatabaseFactory): Database = factory.createDatabase()
}
