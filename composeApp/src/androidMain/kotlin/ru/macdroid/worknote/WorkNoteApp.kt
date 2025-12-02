package ru.macdroid.worknote

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.macdroid.worknote.datasources.di.loadKoinApiModules
import ru.macdroid.worknote.datasources.di.loadKoinRoomModules
import ru.macdroid.worknote.datasources.di.loadKoinStorageModules
import ru.macdroid.worknote.di.loadKoinDeviceModules
import ru.macdroid.worknote.di.loadKoinLoggerModules
import ru.macdroid.worknote.di.loadKoinViewModelModules
import ru.macdroid.worknote.features.s05_e01_chat.di.loadKoinChatModules
import ru.macdroid.worknote.features.s05_e02_weather.di.loadKoinWeatherModules

class WorkNoteApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WorkNoteApp)
            modules(
                modules =
                    loadKoinChatModules() +
                            loadKoinStorageModules() +
                            loadKoinLoggerModules() +
                            loadKoinApiModules() +
                            loadKoinRoomModules() +
                            loadKoinViewModelModules() +
                            loadKoinDeviceModules() +
                            loadKoinWeatherModules()
            )
        }
    }
}

