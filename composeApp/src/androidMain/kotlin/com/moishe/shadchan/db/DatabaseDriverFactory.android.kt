package com.moishe.shadchan.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.moishe.shadchan.platform.PlatformContext

actual class DatabaseDriverFactory actual constructor(private val context: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ShadchanDatabase.Schema,
            context = context.androidContext,
            name = "shadchan.db",
            callback = object : AndroidSqliteDriver.Callback(ShadchanDatabase.Schema) {
                override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            }
        )
    }
}
