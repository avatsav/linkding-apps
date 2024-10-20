package dev.avatsav.linkding.data.db.inject

import com.r0adkll.kimchi.annotations.ContributesTo
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.DatabaseFactory
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Provides

expect interface SqlDelightPlatformComponent

@ContributesTo(AppScope::class)
interface SqlDelightComponent : SqlDelightPlatformComponent {
    @SingleIn(AppScope::class)
    @Provides
    fun provideSqlDelightDatabase(
        factory: DatabaseFactory,
    ): Database = factory.createDatabase()
}
