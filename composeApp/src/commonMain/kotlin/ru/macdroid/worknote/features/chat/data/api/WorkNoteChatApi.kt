package ru.macdroid.worknote.features.chat.data.api

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.macdroid.worknote.features.chat.data.dto.ClaudeRequestDTO
import ru.macdroid.worknote.features.chat.data.dto.ClaudeResponseDTO

class WorkNoteChatApi(
    private val logger: Logger,
    private val client: HttpClient
) {
    suspend fun sendMessage(message: ClaudeRequestDTO): Result<ClaudeResponseDTO> =
        runCatching {
            logger.d { "🚀 WorkNoteChatApi: Отправка сообщения в Claude API" }

            client.post("https://api.anthropic.com/v1/messages") {
                contentType(ContentType.Application.Json)
                setBody(
                    ClaudeRequestDTO(
                        model = message.model,
                        maxTokens = message.maxTokens,
                        messages = message.messages
                    )
                )
            }
        }.mapCatching { response ->
            logger.v { "📡 WorkNoteChatApi: Url: ${response.request.url}" }
            logger.v { "📋 WorkNoteChatApi: Headers: ${response.request.headers}" }
            logger.d { "✅ WorkNoteChatApi: Response status: ${response.status}" }

            response.body<ClaudeResponseDTO>()
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