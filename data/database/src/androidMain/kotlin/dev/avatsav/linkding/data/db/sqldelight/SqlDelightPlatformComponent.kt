package dev.avatsav.linkding.data.db.sqldelight

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

actual interface SqlDelightPlatformComponent {

    @Provides
    @AppScope
    fun provideSqliteDriver(
        application: Application,
    ): SqlDriver {
        TODO()
//        return AndroidSqliteDriver(
//            schema = Database.Schema,
//            context = application,
//            name = "bookmarks.db",
//            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
//                override fun onConfigure(db: SupportSQLiteDatabase) {
//                    db.enableWriteAheadLogging()
//                    db.setForeignKeyConstraintsEnabled(true)
//                }
//            },
//        )
    }
}
