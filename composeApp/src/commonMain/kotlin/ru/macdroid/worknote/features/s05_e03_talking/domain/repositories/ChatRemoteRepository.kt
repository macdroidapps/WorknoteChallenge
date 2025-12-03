package ru.macdroid.worknote.features.s05_e03_talking.domain.repositories

import ru.macdroid.worknote.features.s05_e03_talking.data.dto.ClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e03_talking.domain.models.ClaudeRequestModel

interface ChatRemoteRepository {
    suspend fun sendMessage(
        message: ClaudeRequestModel
    ): Result<ClaudeResponseDTO>
}