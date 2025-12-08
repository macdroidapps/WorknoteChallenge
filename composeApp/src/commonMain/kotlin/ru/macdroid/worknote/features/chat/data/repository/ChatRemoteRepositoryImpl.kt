package ru.macdroid.worknote.features.chat.data.repository

import co.touchlab.kermit.Logger
import ru.macdroid.worknote.features.chat.data.api.WorkNoteChatApi
import ru.macdroid.worknote.features.chat.data.dto.ClaudeRequestDTO
import ru.macdroid.worknote.features.chat.data.dto.ClaudeResponseDTO
import ru.macdroid.worknote.features.chat.data.dto.MessageDTO
import ru.macdroid.worknote.features.chat.domain.models.ClaudeRequestModel
import ru.macdroid.worknote.features.chat.domain.repositories.ChatRemoteRepository

internal class ChatRemoteRepositoryImpl(
    private val api: WorkNoteChatApi,
    private val logger: Logger,
) : ChatRemoteRepository {
    override suspend fun sendMessage(message: ClaudeRequestModel): Result<ClaudeResponseDTO> {
        val request = ClaudeRequestDTO(
            model = message.model,
            maxTokens = message.maxTokens,
            messages = message.messages.map {
                MessageDTO(
                    role = it.role,
                    content = it.content
                )
            },
            temperature = message.temperature
        )
        return api.sendMessage(request)
    }
}
