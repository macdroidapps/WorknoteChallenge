package ru.macdroid.worknote.di

import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.features.s05_e01_chat.domain.usecases.SendMessageUseCase

import ru.macdroid.worknote.features.s05_e01_chat.presentation.ChatViewModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.usecases.SendWeatherMessageUseCase
import ru.macdroid.worknote.features.s05_e02_weather.presentation.WeatherViewModel

private val viewModelsModule = module {
    viewModel {
        ChatViewModel(
            sendMessageUseCase = get<SendMessageUseCase>(),
            logger = get<Logger>()
        )
    }

    viewModel {
        WeatherViewModel(
            sendMessageUseCase = get<SendWeatherMessageUseCase>(),
            logger = get<Logger>()
        )
    }
}

fun loadKoinViewModelModules(): List<Module> = listOf(
    viewModelsModule
)

