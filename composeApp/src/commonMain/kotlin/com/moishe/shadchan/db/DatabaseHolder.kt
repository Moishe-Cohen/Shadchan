package com.moishe.shadchan.db

import com.moishe.shadchan.platform.PlatformContext

/**
 * Holds the single ShadchanDatabase instance. closeAndReset() is used after a restore-from-
 * backup so the next access reopens the replaced database file cleanly (mirrors the original
 * Room AppDatabase.closeAndReset()).
 */
object DatabaseHolder {
    private var instance: ShadchanDatabase? = null
    private var driverFactory: DatabaseDriverFactory? = null

    fun get(context: PlatformContext): ShadchanDatabase {
        instance?.let { return it }
        synchronized(this) {
            instance?.let { return it }
            val factory = DatabaseDriverFactory(context)
            driverFactory = factory
            val db = ShadchanDatabase(factory.createDriver())
            instance = db
            return db
        }
    }

    fun closeAndReset() {
        synchronized(this) {
            instance = null
            driverFactory = null
        }
    }
}
