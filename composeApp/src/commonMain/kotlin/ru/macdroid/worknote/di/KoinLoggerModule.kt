package ru.macdroid.worknote.di

import co.touchlab.kermit.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

// Модуль для Logger
val loggerModule = module {
    single {
        Logger.withTag("WorkNote")
    }
}

fun loadKoinLoggerModules(): List<Module> = listOf(
    loggerModule
)