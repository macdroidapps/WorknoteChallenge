package ru.macdroid.worknote.datasources.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.datasources.network.createHttpClient
import ru.macdroid.worknote.features.chat.data.api.WorkNoteChatApi

private val networkClientModule = module {
    single {
        createHttpClient()
    }
}
private val apiModule = module {
    single {
        WorkNoteChatApi(
            logger = get<Logger>(),
            client = get<HttpClient>()
        )
    }
}

fun loadKoinApiModules(): List<Module> = listOf(
    networkClientModule,
    apiModule
)