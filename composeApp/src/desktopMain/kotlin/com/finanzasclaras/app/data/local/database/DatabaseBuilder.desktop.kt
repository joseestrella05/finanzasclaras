package com.finanzasclaras.app.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val userHome = System.getProperty("user.home")
    val appDir = File(userHome, ".finanzasclaras")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    val dbFile = File(appDir, "finanzas_claras_db.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
}
