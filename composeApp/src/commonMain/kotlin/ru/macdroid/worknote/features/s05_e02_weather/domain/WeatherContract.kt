package ru.macdroid.worknote.features.s05_e02_weather.domain


import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherMessageModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherResponse


data class WeatherState(
    val chatMessages: List<WeatherMessageModel> = emptyList(),
    val isLoading: Boolean = false,
    val currentMessage: String? = null
)

sealed class WeatherEvent {
    data class SendMessageToChat(val message: String) : WeatherEvent()
    data class UpdateCurrentMessage(val message: String) : WeatherEvent()
}

sealed class WeatherEffect {
    data object NavigateNext : WeatherEffect()
    data class ShowError(val message: String) : WeatherEffect()
}

