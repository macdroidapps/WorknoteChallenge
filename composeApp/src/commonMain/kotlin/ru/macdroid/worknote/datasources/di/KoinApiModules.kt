package ru.macdroid.worknote.datasources.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.features.s05_e01_chat.data.api.WorkNoteChatApi
import ru.macdroid.worknote.features.s05_e02_weather.data.api.WeatherApi
import ru.macdroid.worknote.datasources.network.createHttpClient
import ru.macdroid.worknote.features.s05_e03_talking.data.api.WorkNoteTalkApi

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
    single {
        WeatherApi(
            logger = get<Logger>(),
            client = get<HttpClient>()
        )
    }
    single {
        WorkNoteTalkApi(
            logger = get<Logger>(),
            client = get<HttpClient>()
        )
    }
}

fun loadKoinApiModules(): List<Module> = listOf(
    networkClientModule,
    apiModule
)