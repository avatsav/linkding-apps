package dev.avatsav.linkding.data.db.inject

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.DatabaseFactory
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

expect interface SqlDelightPlatformComponent

@ContributesTo(AppScope::class)
interface SqlDelightComponent : SqlDelightPlatformComponent {
  @SingleIn(AppScope::class)
  @Provides
  fun provideSqlDelightDatabase(factory: DatabaseFactory): Database = factory.createDatabase()
}
