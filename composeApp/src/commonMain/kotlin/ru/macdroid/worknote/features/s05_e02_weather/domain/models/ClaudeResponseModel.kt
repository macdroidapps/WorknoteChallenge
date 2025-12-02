package ru.macdroid.worknote.features.s05_e02_weather.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherClaudeResponseModel(
    val content: List<WeatherResponseContent>?,
    val id: String?,
    val model: String?,
    val role: String?,
    val stop_reason: String?,
    val stop_sequence: String?,
    val type: String?,
    val usage: WeatherUsage?
)

@Serializable
data class WeatherResponseContent(
    val text: String?,
    val type: String?
)

@Serializable
data class WeatherUsage(
    val cache_creation: WeatherCacheCreation?,
    val cache_creation_input_tokens: Int?,
    val cache_read_input_tokens: Int?,
    val input_tokens: Int?,
    val output_tokens: Int?,
    val service_tier: String?
)

@Serializable
data class WeatherCacheCreation(
    val ephemeral_1h_input_tokens: Int?,
    val ephemeral_5m_input_tokens: Int?
)