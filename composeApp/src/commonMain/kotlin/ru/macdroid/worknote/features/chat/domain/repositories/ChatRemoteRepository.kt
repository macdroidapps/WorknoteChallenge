package ru.macdroid.worknote.features.chat.domain.repositories

import ru.macdroid.worknote.features.chat.data.dto.ClaudeResponseDTO
import ru.macdroid.worknote.features.chat.domain.models.ClaudeRequestModel

interface ChatRemoteRepository {
    suspend fun sendMessage(
        message: ClaudeRequestModel
    ): Result<ClaudeResponseDTO>
}