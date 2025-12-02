package ru.macdroid.worknote.features.s05_e02_weather.domain.repositories

import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherClaudeRequestModel

interface WeatherRemoteRepository {
    suspend fun sendMessage(
        message: WeatherClaudeRequestModel
    ): Result<WeatherClaudeResponseDTO>
}