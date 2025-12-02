package ru.macdroid.worknote.features.s05_e02_weather.di

import co.touchlab.kermit.Logger
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.macdroid.worknote.features.s05_e02_weather.data.api.WeatherApi
import ru.macdroid.worknote.features.s05_e02_weather.data.repository.WeatherRemoteRepositoryImpl
import ru.macdroid.worknote.features.s05_e02_weather.domain.repositories.WeatherRemoteRepository
import ru.macdroid.worknote.features.s05_e02_weather.domain.usecases.SendWeatherMessageUseCase

private val WeatherRepositoryModule = module {
    single<WeatherRemoteRepository> {
        WeatherRemoteRepositoryImpl(
            api = get<WeatherApi>(),
            logger = get<Logger>()
        )
    }
}

private val WeatherUseCaseModule = module {
    single { SendWeatherMessageUseCase(repository = get<WeatherRemoteRepository>()) }
}


fun loadKoinWeatherModules(): List<Module> = listOf(
    WeatherRepositoryModule,
    WeatherUseCaseModule
)