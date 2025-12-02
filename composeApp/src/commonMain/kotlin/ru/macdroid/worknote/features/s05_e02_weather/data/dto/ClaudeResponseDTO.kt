package ru.macdroid.worknote.features.s05_e02_weather.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherClaudeResponseDTO(
    val content: List<WeatherResponseContentDTO>?,
    val id: String?,
    val model: String?,
    val role: String?,
    val stop_reason: String?,
    val stop_sequence: String?,
    val type: String?,
    val usage: WeatherUsageDTO?
)

@Serializable
data class WeatherResponseContentDTO(
    val text: String?,
    val type: String?
)

@Serializable
data class WeatherUsageDTO(
    val cache_creation: WeatherCacheCreationDTO?,
    val cache_creation_input_tokens: Int?,
    val cache_read_input_tokens: Int?,
    val input_tokens: Int?,
    val output_tokens: Int?,
    val service_tier: String?
)

@Serializable
data class WeatherCacheCreationDTO(
    val ephemeral_1h_input_tokens: Int?,
    val ephemeral_5m_input_tokens: Int?
)