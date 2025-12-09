package ru.macdroid.worknote.di

import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.features.chat.data.api.HuggingFaceApi
import ru.macdroid.worknote.features.chat.presentation.ChatViewModel

private val viewModelsModule = module {
    viewModel {
        ChatViewModel(
            huggingFaceApi = get<HuggingFaceApi>(),
            logger = get<Logger>()
        )
    }
}

fun loadKoinViewModelModules(): List<Module> = listOf(
    viewModelsModule
)

