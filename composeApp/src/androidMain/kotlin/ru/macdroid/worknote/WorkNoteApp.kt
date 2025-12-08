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
import ru.macdroid.worknote.features.chat.di.loadKoinChatModules

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
                            loadKoinDeviceModules()
            )
        }
    }
}

