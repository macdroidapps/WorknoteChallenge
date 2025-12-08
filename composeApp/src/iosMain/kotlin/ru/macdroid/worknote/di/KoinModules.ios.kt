package ru.macdroid.worknote.di

import co.touchlab.kermit.Logger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.di.loadKoinApiModules
import ru.macdroid.worknote.datasources.di.loadKoinRoomModules
import ru.macdroid.worknote.datasources.di.loadKoinStorageModules
import ru.macdroid.worknote.features.chat.di.loadKoinChatModules
import ru.macdroid.worknote.features.chat.domain.usecases.SendMessageUseCase
import ru.macdroid.worknote.features.chat.presentation.ChatViewModel

private val viewModelModule = module {
    factory {
        ChatViewModel(
            sendMessageUseCase = get<SendMessageUseCase>(),
            logger = get<Logger>()
        )
    }
}

fun loadKoinViewModelModules(): List<Module> = listOf(
    viewModelModule
)

/**
 * Инициализация Koin для iOS
 * Вызывается из Swift при старте приложения
 */
fun initKoinIos() {
    startKoin {
        modules(
            modules =
                loadKoinApiModules() +
                        loadKoinLoggerModules() +
                        loadKoinChatModules() +
                        loadKoinStorageModules() +
                        loadKoinRoomModules() +
                        loadKoinDeviceModules() +
                        loadKoinViewModelModules()
        )
    }
}

