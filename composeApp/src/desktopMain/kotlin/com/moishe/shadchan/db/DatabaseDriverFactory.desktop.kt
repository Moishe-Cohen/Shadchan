package com.moishe.shadchan.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.moishe.shadchan.platform.PlatformContext
import java.io.File

actual class DatabaseDriverFactory actual constructor(private val context: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        val appDir = File(System.getProperty("user.home"), ".shadchan").apply { mkdirs() }
        val dbFile = File(appDir, "shadchan.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        driver.execute(null, "PRAGMA foreign_keys=ON;", 0)
        if (!dbFile.exists() || dbFile.length() == 0L) {
            ShadchanDatabase.Schema.create(driver)
        } else {
            // Make sure schema is current if the app was updated (SQLDelight migrations
            // are wired in automatically once schema versions increase).
            ShadchanDatabase.Schema.migrate(driver, 1, ShadchanDatabase.Schema.version)
        }
        return driver
    }

    companion object {
        fun databaseDirectory(): File = File(System.getProperty("user.home"), ".shadchan").apply { mkdirs() }
    }
}
