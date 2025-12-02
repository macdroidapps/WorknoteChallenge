package ru.macdroid.worknote.datasources.di

import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

private val storageModule = module {
    single<Settings> { Settings() }
}

fun loadKoinStorageModules(): List<Module> = listOf(
    storageModule
)