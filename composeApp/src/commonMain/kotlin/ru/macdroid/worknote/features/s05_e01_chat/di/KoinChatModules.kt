package ru.macdroid.worknote.features.s05_e01_chat.di

import co.touchlab.kermit.Logger
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.local.settings.ChatStorageImpl
import ru.macdroid.worknote.features.s05_e01_chat.data.api.WorkNoteChatApi
import ru.macdroid.worknote.features.s05_e01_chat.data.repository.ChatRemoteRepositoryImpl
import ru.macdroid.worknote.features.s05_e01_chat.domain.repositories.ChatRemoteRepository
import ru.macdroid.worknote.features.s05_e01_chat.domain.repositories.ChatStorage
import ru.macdroid.worknote.features.s05_e01_chat.domain.usecases.SendMessageUseCase

private val chatRepositoryModule = module {
    single<ChatRemoteRepository> {
        ChatRemoteRepositoryImpl(
            api = get<WorkNoteChatApi>(),
            logger = get<Logger>()
        )
    }
}

private val chatUseCaseModule = module {
    single { SendMessageUseCase(repository = get<ChatRemoteRepository>()) }
}

private val chatStorageModule = module {
    single<ChatStorage> { ChatStorageImpl(settings = get()) }
}

fun loadKoinChatModules(): List<Module> = listOf(
    chatRepositoryModule,
    chatUseCaseModule,
    chatStorageModule
)