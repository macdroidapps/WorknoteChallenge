package ru.macdroid.worknote.features.s05_e03_talking.di

import co.touchlab.kermit.Logger
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.features.s05_e03_talking.data.api.WorkNoteTalkApi
import ru.macdroid.worknote.features.s05_e03_talking.data.repository.ChatRemoteRepositoryImpl
import ru.macdroid.worknote.features.s05_e03_talking.domain.repositories.ChatRemoteRepository
import ru.macdroid.worknote.features.s05_e03_talking.domain.usecases.SendTalkMessageUseCase

private val chatRepositoryModule = module {
    single<ChatRemoteRepository> {
        ChatRemoteRepositoryImpl(
            api = get<WorkNoteTalkApi>(),
            logger = get<Logger>()
        )
    }
}

private val chatUseCaseModule = module {
    single { SendTalkMessageUseCase(repository = get<ChatRemoteRepository>()) }
}



fun loadKoinTalkModules(): List<Module> = listOf(
    chatRepositoryModule,
    chatUseCaseModule
)