package ru.macdroid.worknote.features.s05_e02_weather.domain.mappers

import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherCacheCreationDTO
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherClaudeResponseDTO
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherResponseContentDTO
import ru.macdroid.worknote.features.s05_e02_weather.data.dto.WeatherUsageDTO
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherCacheCreation
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherClaudeResponseModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherResponseContent
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherUsage


fun WeatherClaudeResponseDTO.toModel(): WeatherClaudeResponseModel {
    return WeatherClaudeResponseModel(
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

fun WeatherResponseContentDTO.toModel(): WeatherResponseContent {
    return WeatherResponseContent(
        text = text,
        type = type
    )
}

fun WeatherUsageDTO.toModel(): WeatherUsage {
    return WeatherUsage(
        cache_creation = cache_creation?.toModel(),
        cache_creation_input_tokens = cache_creation_input_tokens,
        cache_read_input_tokens = cache_read_input_tokens,
        input_tokens = input_tokens,
        output_tokens = output_tokens,
        service_tier = service_tier
    )
}

fun WeatherCacheCreationDTO.toModel(): WeatherCacheCreation {
    return WeatherCacheCreation(
        ephemeral_1h_input_tokens = ephemeral_1h_input_tokens,
        ephemeral_5m_input_tokens = ephemeral_5m_input_tokens
    )
}