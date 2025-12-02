package ru.macdroid.worknote.features.s05_e01_chat.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.macdroid.worknote.features.s05_e01_chat.domain.mappers.toModel
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.ClaudeRequestModel
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.ClaudeResponseModel
import ru.macdroid.worknote.features.s05_e01_chat.domain.repositories.ChatRemoteRepository

class SendMessageUseCase(
    private val repository: ChatRemoteRepository
) {
    operator fun invoke(message: ClaudeRequestModel): Flow<Result<ClaudeResponseModel>> = flow {
        val response = repository
            .sendMessage(message)
            .map { responseDto ->
                responseDto.toModel()
            }
        emit(response)
    }
}
