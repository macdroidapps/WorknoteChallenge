package ru.macdroid.worknote.di

import co.touchlab.kermit.Logger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.di.loadKoinApiModules
import ru.macdroid.worknote.datasources.di.loadKoinRoomModules
import ru.macdroid.worknote.datasources.di.loadKoinStorageModules
import ru.macdroid.worknote.features.s05_e01_chat.di.loadKoinChatModules
import ru.macdroid.worknote.features.s05_e01_chat.domain.usecases.SendMessageUseCase
import ru.macdroid.worknote.features.s05_e01_chat.presentation.ChatViewModel
import ru.macdroid.worknote.features.s05_e02_weather.di.loadKoinWeatherModules
import ru.macdroid.worknote.features.s05_e02_weather.domain.usecases.SendWeatherMessageUseCase
import ru.macdroid.worknote.features.s05_e02_weather.presentation.WeatherViewModel
import ru.macdroid.worknote.features.s05_e03_talking.di.loadKoinTalkModules
import ru.macdroid.worknote.features.s05_e03_talking.domain.usecases.SendTalkMessageUseCase
import ru.macdroid.worknote.features.s05_e03_talking.presentation.TalkViewModel

private val viewModelModule = module {
    factory {
        ChatViewModel(
            sendMessageUseCase = get<SendMessageUseCase>(),
            logger = get<Logger>()
        )
    }
    factory {
        WeatherViewModel(
            sendMessageUseCase = get<SendWeatherMessageUseCase>(),
            logger = get<Logger>()
        )
    }
    factory {
        TalkViewModel(
            sendMessageUseCase = get<SendTalkMessageUseCase>(),
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
        modules(modules =
                    loadKoinApiModules() +
                    loadKoinLoggerModules() +
                    loadKoinChatModules() +
                    loadKoinStorageModules() +
                    loadKoinRoomModules() +
                    loadKoinDeviceModules() +
                    loadKoinViewModelModules() +
                    loadKoinWeatherModules() +
                            loadKoinTalkModules()
        )
    }
}

