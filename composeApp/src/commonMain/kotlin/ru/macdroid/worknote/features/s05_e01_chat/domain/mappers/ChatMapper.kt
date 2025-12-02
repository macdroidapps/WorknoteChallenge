package ru.macdroid.worknote.features.s05_e01_chat.domain.mappers

import ru.macdroid.worknote.features.s05_e01_chat.data.dto.CacheCreationDTO
import ru.macdroid.worknote.features.s05_e01_chat.data.dto.ClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e01_chat.data.dto.ResponseContentDTO
import ru.macdroid.worknote.features.s05_e01_chat.data.dto.UsageDTO
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.CacheCreation
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.ClaudeResponseModel
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.ResponseContent
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.Usage


fun ClaudeResponseDTO.toModel(): ClaudeResponseModel {
    return ClaudeResponseModel(
        content = content?.map { it.toModel() },
        id = id,
        model = model,
        role = role,
        stop_reason = stop_reason,
        stop_sequence = stop_sequence,
        type = type,
        usage = usage?.toModel()
    )
}

fun ResponseContentDTO.toModel(): ResponseContent {
    return ResponseContent(
        text = text,
        type = type
    )
}

fun UsageDTO.toModel(): Usage {
    return Usage(
        cache_creation = cache_creation?.toModel(),
        cache_creation_input_tokens = cache_creation_input_tokens,
        cache_read_input_tokens = cache_read_input_tokens,
        input_tokens = input_tokens,
        output_tokens = output_tokens,
        service_tier = service_tier
    )
}

fun CacheCreationDTO.toModel(): CacheCreation {
    return CacheCreation(
        ephemeral_1h_input_tokens = ephemeral_1h_input_tokens,
        ephemeral_5m_input_tokens = ephemeral_5m_input_tokens
    )
}