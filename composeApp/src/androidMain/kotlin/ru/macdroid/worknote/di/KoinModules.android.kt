package ru.macdroid.worknote.di

import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.features.chat.domain.usecases.SendMessageUseCase
import ru.macdroid.worknote.features.chat.presentation.ChatViewModel

private val viewModelsModule = module {
    viewModel {
        ChatViewModel(
            sendMessageUseCase = get<SendMessageUseCase>(),
            logger = get<Logger>()
        )
    }
}

fun loadKoinViewModelModules(): List<Module> = listOf(
    viewModelsModule
)

