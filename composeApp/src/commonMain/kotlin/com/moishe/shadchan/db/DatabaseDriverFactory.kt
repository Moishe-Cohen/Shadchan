package com.moishe.shadchan.db

import app.cash.sqldelight.db.SqlDriver
import com.moishe.shadchan.platform.PlatformContext

expect class DatabaseDriverFactory(context: PlatformContext) {
    fun createDriver(): SqlDriver
}
