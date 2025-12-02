package ru.macdroid.worknote.features.s05_e02_weather.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okio.FileSystem

@Serializable
data class WeatherClaudeRequestDTO(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val system: String,
    val messages: List<WeatherMessageDTO>
)

@Serializable
data class WeatherMessageDTO(
    val role: String,
    val content: String
)