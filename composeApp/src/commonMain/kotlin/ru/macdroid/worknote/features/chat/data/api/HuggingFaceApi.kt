package ru.macdroid.worknote.features.chat.data.api

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceMessageDTO
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceRequestDTO
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceResponseDTO
import ru.macdroid.worknote.utils.AppConstants

class HuggingFaceApi(
    private val logger: Logger,
    private val client: HttpClient
) {
    suspend fun sendMessage(request: HuggingFaceRequestDTO): Result<HuggingFaceResponseDTO> =
        runCatching {
            logger.d { "🚀 HuggingFaceApi: Отправка сообщения в HuggingFace API, модель: ${request.model}" }

            client.post("https://router.huggingface.co/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${AppConstants.HUGGING_FACE_API_KEY}")
                setBody(
                    HuggingFaceRequestDTO(
                        model = request.model,
                        messages = request.messages,
                        maxTokens = request.maxTokens,
                        stream = false
                    )
                )
            }
        }.mapCatching { response ->
            logger.v { "📡 HuggingFaceApi: Url: ${response.request.url}" }
            logger.v { "📋 HuggingFaceApi: Headers: ${response.request.headers}" }
            logger.d { "✅ HuggingFaceApi: Response status: ${response.status}" }

            response.body<HuggingFaceResponseDTO>()
        }.onSuccess { result ->
            logger.d {
                "✅ HuggingFaceApi: Успешный ответ. Tokens: input=${result.usage?.promptTokens}, output=${result.usage?.completionTokens}"
            }
        }.onFailure { throwable ->
            logger.e(throwable) { "❌ HuggingFaceApi: Ошибка при отправке сообщения - ${throwable.message}" }
        }
}

