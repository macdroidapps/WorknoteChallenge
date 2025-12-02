package ru.macdroid.worknote.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.local.device.DeviceIdProvider

/**
 * Модуль для iOS-специфичных зависимостей (Device ID)
 */
private val deviceModule = module {
    single { DeviceIdProvider() }
}

fun loadKoinDeviceModules(): List<Module> = listOf(
    deviceModule
)

