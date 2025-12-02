package ru.macdroid.worknote.features.s05_e02_weather.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.macdroid.worknote.features.s05_e02_weather.domain.mappers.toModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherClaudeRequestModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherClaudeResponseModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.repositories.WeatherRemoteRepository

class SendWeatherMessageUseCase(
    private val repository: WeatherRemoteRepository
) {
    operator fun invoke(message: WeatherClaudeRequestModel): Flow<Result<WeatherClaudeResponseModel>> =
        flow {
            val response = repository
                .sendMessage(message)
                .map { responseDto ->
                    responseDto.toModel()
                }
            emit(response)
        }
}
