package ru.macdroid.worknote.features.s05_e01_chat.domain.repositories

import ru.macdroid.worknote.features.s05_e01_chat.data.dto.ClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.ClaudeRequestModel

interface ChatRemoteRepository {
    suspend fun sendMessage(
        message: ClaudeRequestModel
    ): Result<ClaudeResponseDTO>
}