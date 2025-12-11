package ru.macdroid.worknote.features.chat.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.local.settings.ChatStorageImpl
import ru.macdroid.worknote.features.chat.data.api.HuggingFaceApi
import ru.macdroid.worknote.features.chat.data.api.WorkNoteChatApi
import ru.macdroid.worknote.features.chat.data.repository.ChatRemoteRepositoryImpl
import ru.macdroid.worknote.features.chat.domain.repositories.ChatRemoteRepository
import ru.macdroid.worknote.features.chat.domain.repositories.ChatStorage
import ru.macdroid.worknote.features.chat.domain.repositories.ConversationRepository
import ru.macdroid.worknote.features.chat.domain.repositories.ConversationRepositoryImpl
import ru.macdroid.worknote.features.chat.domain.usecases.DialogueCompressionUseCase
import ru.macdroid.worknote.features.chat.domain.usecases.SendHuggingFaceMessageUseCase
import ru.macdroid.worknote.features.chat.domain.usecases.SendMessageUseCase
import ru.macdroid.worknote.features.chat.domain.models.CompressionConfig
import ru.macdroid.worknote.features.chat.domain.utils.TokenCounter
import ru.macdroid.worknote.features.chat.domain.utils.SimpleTokenCounter

private val chatApiModule = module {
    single { HuggingFaceApi(logger = get<Logger>(), client = get<HttpClient>()) }
}

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
    single { SendHuggingFaceMessageUseCase(api = get<HuggingFaceApi>()) }
}

private val chatStorageModule = module {
    single<ChatStorage> { ChatStorageImpl(settings = get()) }
}

// Модули для системы сжатия диалогов
private val compressionModule = module {
    // Конфигурация сжатия
    single {
        CompressionConfig(
            compressionThreshold = 10,
            keepRecentMessages = 3,
            maxTokensBeforeCompression = 8000,
            compressionRatio = 0.3f
        )
    }

    // Счетчик токенов
    single<TokenCounter> { SimpleTokenCounter() }

    // Use case для сжатия диалогов
    single {
        DialogueCompressionUseCase(
            huggingFaceApi = get(),
            tokenCounter = get(),
            config = get(),
            logger = get()
        )
    }

    // Репозиторий разговоров с поддержкой сжатия
    single<ConversationRepository> {
        ConversationRepositoryImpl(
            compressionUseCase = get(),
            config = get(),
            tokenCounter = get(),
            logger = get()
        )
    }
}

fun loadKoinChatModules(): List<Module> = listOf(
    chatApiModule,
    chatRepositoryModule,
    chatUseCaseModule,
    chatStorageModule,
    compressionModule
)