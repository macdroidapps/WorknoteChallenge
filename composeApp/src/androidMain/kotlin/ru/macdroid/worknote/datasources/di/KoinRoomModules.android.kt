package ru.macdroid.worknote.datasources.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.local.db.AppDatabase
import ru.macdroid.worknote.datasources.local.db.TodoDao
import ru.macdroid.worknote.datasources.local.db.getDatabaseBuilder
import ru.macdroid.worknote.datasources.local.db.getRoomDatabase

private val databaseModule = module {
    single<AppDatabase>(createdAtStart = true) {
        val context = androidContext()
        val builder = getDatabaseBuilder(context = context)
        getRoomDatabase(builder = builder)
    }
}

private val daoModule = module {
    single<TodoDao> { get<AppDatabase>().getDao() }
}

fun loadKoinRoomModules(): List<Module> = listOf(
    databaseModule,
    daoModule
)

