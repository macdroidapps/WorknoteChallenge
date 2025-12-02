package ru.macdroid.worknote.features.s05_e02_weather.data.api

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherClaudeRequestDTO
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherClaudeResponseDTO

class WeatherApi(
    private val logger: Logger,
    private val client: HttpClient
) {
    suspend fun sendMessage(message: WeatherClaudeRequestDTO): Result<WeatherClaudeResponseDTO> =
        runCatching {
            logger.d { "🚀 WorkNoteChatApi: Отправка сообщения в Claude API" }

            client.post("https://api.anthropic.com/v1/messages") {
                contentType(ContentType.Application.Json)
                setBody(
                    WeatherClaudeRequestDTO(
                        model = message.model,
                        maxTokens = message.maxTokens,
                        messages = message.messages,
                        system = message.system
                    )
                )
            }
        }.mapCatching { response ->
            logger.v { "📡 WorkNoteChatApi: Url: ${response.request.url}" }
            logger.v { "📋 WorkNoteChatApi: Headers: ${response.request.headers}" }
            logger.d { "✅ WorkNoteChatApi: Response status: ${response.status}" }
            logger.d { "✅ WorkNoteChatApi: Response body: ${response.body<WeatherClaudeResponseDTO>()}" }

            response.body<WeatherClaudeResponseDTO>()
        }.onSuccess { result ->
            logger.d {
                "✅ WorkNoteChatApi: Успешный ответ от Claude. Content: ${
                    result.content?.firstOrNull()?.text?.take(
                        100
                    )
                }..."
            }
        }.onFailure { throwable ->
            logger.e(throwable) { "❌ WorkNoteChatApi: Ошибка при отправке сообщения - ${throwable.message}" }
        }
}