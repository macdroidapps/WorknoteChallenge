package ru.macdroid.worknote.features.s05_e02_weather.data.repository

import co.touchlab.kermit.Logger
import ru.macdroid.worknote.features.s05_e02_weather.data.api.WeatherApi
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherClaudeRequestDTO
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherMessageDTO
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherClaudeRequestModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.repositories.WeatherRemoteRepository
import ru.macdroid.worknote.utils.getWeatherSystemPrompt

internal class WeatherRemoteRepositoryImpl(
    private val api: WeatherApi,
    private val logger: Logger,
) : WeatherRemoteRepository {
    override suspend fun sendMessage(message: WeatherClaudeRequestModel): Result<WeatherClaudeResponseDTO> {
        val request = WeatherClaudeRequestDTO(
            model = message.model,
            maxTokens = message.maxTokens,
            system = message.system,
            messages = message.messages.map {
                WeatherMessageDTO(
                    role = it.role,
                    content = it.content + getWeatherSystemPrompt()
                )
            }
        )
        return api.sendMessage(request)
    }
}
