package ru.macdroid.worknote.features.chat.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.macdroid.worknote.features.chat.data.api.HuggingFaceApi
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceMessageDTO
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceRequestDTO
import ru.macdroid.worknote.features.chat.domain.models.HuggingFaceResponseModel
import ru.macdroid.worknote.features.chat.domain.models.MessageModel

class SendHuggingFaceMessageUseCase(
    private val api: HuggingFaceApi
) {
    operator fun invoke(
        model: String,
        messages: List<MessageModel>,
        maxTokens: Int = 1024
    ): Flow<Result<HuggingFaceResponseModel>> = flow {
        val request = HuggingFaceRequestDTO(
            model = model,
            messages = messages.map {
                HuggingFaceMessageDTO(role = it.role, content = it.content)
            },
            maxTokens = maxTokens,
            stream = false
        )

        val result = api.sendMessage(request).map { response ->
            HuggingFaceResponseModel(
                content = response.choices?.firstOrNull()?.message?.content ?: "",
                model = response.model,
                inputTokens = response.usage?.promptTokens,
                outputTokens = response.usage?.completionTokens,
                totalTokens = response.usage?.totalTokens
            )
        }

        emit(result)
    }
}

