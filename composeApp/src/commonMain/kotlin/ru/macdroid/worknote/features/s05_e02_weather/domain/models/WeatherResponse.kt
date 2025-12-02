package ru.macdroid.worknote.features.s05_e02_weather.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val city: String,
    val temperature: Int,
)