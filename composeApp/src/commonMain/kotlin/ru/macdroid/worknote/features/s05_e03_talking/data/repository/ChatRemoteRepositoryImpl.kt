package ru.macdroid.worknote.features.s05_e03_talking.data.repository

import co.touchlab.kermit.Logger
import ru.macdroid.worknote.features.s05_e03_talking.data.api.WorkNoteTalkApi
import ru.macdroid.worknote.features.s05_e03_talking.data.dto.ClaudeRequestDTO
import ru.macdroid.worknote.features.s05_e03_talking.data.dto.ClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e03_talking.data.dto.MessageDTO
import ru.macdroid.worknote.features.s05_e03_talking.domain.models.ClaudeRequestModel
import ru.macdroid.worknote.features.s05_e03_talking.domain.repositories.ChatRemoteRepository
import ru.macdroid.worknote.utils.getTalkSystemPrompt

internal class ChatRemoteRepositoryImpl(
    private val api: WorkNoteTalkApi,
    private val logger: Logger,
) : ChatRemoteRepository {
    override suspend fun sendMessage(message: ClaudeRequestModel): Result<ClaudeResponseDTO> {
        val request = ClaudeRequestDTO(
            model = message.model,
            maxTokens = message.maxTokens,
            messages = message.messages.map {
                MessageDTO(
                    role = it.role,
                    content = it.content + getTalkSystemPrompt()
                )
            }
        )
        return api.sendMessage(request)
    }
}
