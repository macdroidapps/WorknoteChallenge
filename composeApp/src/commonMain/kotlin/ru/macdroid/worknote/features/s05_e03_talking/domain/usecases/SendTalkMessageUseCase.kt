package ru.macdroid.worknote.features.s05_e03_talking.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.macdroid.worknote.features.s05_e03_talking.domain.mappers.toModel
import ru.macdroid.worknote.features.s05_e03_talking.domain.models.ClaudeRequestModel
import ru.macdroid.worknote.features.s05_e03_talking.domain.models.ClaudeResponseModel
import ru.macdroid.worknote.features.s05_e03_talking.domain.repositories.ChatRemoteRepository

class SendTalkMessageUseCase(
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
