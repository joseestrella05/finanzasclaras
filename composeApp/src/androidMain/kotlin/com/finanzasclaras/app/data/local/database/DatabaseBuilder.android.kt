package com.finanzasclaras.app.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

var appAndroidContext: Context? = null

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = checkNotNull(appAndroidContext) { "appAndroidContext must be initialized before accessing database" }
    val dbFile = context.getDatabasePath("finanzas_claras_db.db")
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
