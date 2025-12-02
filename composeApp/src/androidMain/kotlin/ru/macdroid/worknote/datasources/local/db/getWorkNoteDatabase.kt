package ru.macdroid.worknote.datasources.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        context = context.applicationContext,
        name = "worknote.db"
    )
}
