package ru.macdroid.worknote.features.s05_e02_weather.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherClaudeRequestModel(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val system: String,
    val messages: List<WeatherMessageModel>
)

@Serializable
data class WeatherMessageModel(
    val role: String,
    val content: String,
    val weather: WeatherResponse? = null
)