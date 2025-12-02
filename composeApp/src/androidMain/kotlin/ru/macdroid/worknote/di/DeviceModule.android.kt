package ru.macdroid.worknote.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

import ru.macdroid.worknote.datasources.local.device.DeviceIdProvider

/**
 * Модуль для Android-специфичных зависимостей (Device ID)
 */
private val deviceModule = module {
    single { DeviceIdProvider(context = androidContext()) }
}

fun loadKoinDeviceModules(): List<Module> = listOf(
    deviceModule
)

