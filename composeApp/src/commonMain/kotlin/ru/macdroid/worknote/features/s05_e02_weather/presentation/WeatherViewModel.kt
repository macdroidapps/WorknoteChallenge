package ru.macdroid.worknote.features.s05_e02_weather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatEffect
import ru.macdroid.worknote.features.s05_e02_weather.domain.WeatherEvent
import ru.macdroid.worknote.features.s05_e02_weather.domain.WeatherState
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherClaudeRequestModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherMessageModel
import ru.macdroid.worknote.features.s05_e02_weather.domain.models.WeatherResponse
import ru.macdroid.worknote.features.s05_e02_weather.domain.usecases.SendWeatherMessageUseCase
import ru.macdroid.worknote.utils.AppConstants

class WeatherViewModel(
    private val logger: Logger,
    private val sendMessageUseCase: SendWeatherMessageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state = _state
        .onStart {
            logger.d { "🚀 AuthViewModel started" }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            WeatherState()
        )
    private val _effects = Channel<ChatEffect>()
    val effects = _effects.receiveAsFlow()
    private var loginJob: Job? = null
    private val coroutineHandler = CoroutineExceptionHandler { _, throwable ->
        logger.e(throwable) { "❌ Unhandled coroutine error" }
    }

    private fun onEffect(effect: ChatEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.SendMessageToChat -> sendMessageToChat(message = event.message)
            is WeatherEvent.UpdateCurrentMessage -> updateCurrentMessage(event.message)
        }
    }

    private fun updateCurrentMessage(message: String) {
        _state.update {
            it.copy(
                currentMessage = message
            )
        }
    }

    private fun sendMessageToChat(message: String) {
        if (message.isBlank()) return

        val userMessage = WeatherMessageModel(
            role = "user",
            content = message
        )

        _state.update {
            it.copy(
                chatMessages = _state.value.chatMessages + userMessage,
                currentMessage = ""
            )
        }

        loginJob?.cancel()
        loginJob = viewModelScope.launch(coroutineHandler) {

            try {
                withContext(Dispatchers.IO) {
                    val request = WeatherClaudeRequestModel(
                        model = AppConstants.MODEL_SONNET_4_5,
                        maxTokens = 500,
                        messages = _state.value.chatMessages,
                        system = "" // можно задать getWeatherSystemPrompt здесь, и это будет работать, но я задал через промпт для универсальности
                    )
                    sendMessageUseCase(message = request)
                }.collect { res ->
                    res.fold(
                        onSuccess = { response ->

                            val jsonText = response.content?.firstOrNull()?.text ?: throw Exception("Empty response")
                            val weather = try {
                                Json.decodeFromString<WeatherResponse>(jsonText.trim())
                            } catch (e: Exception) {
                                val cleanJson = jsonText
                                    .replace("```json", "")
                                    .replace("```", "")
                                    .trim()
                                Json.decodeFromString<WeatherResponse>(cleanJson)
                            }

                            logger.d("🌤️ Parsed weather response: City=${weather.city}, Temperature=${weather.temperature}")

                            val claudeMessage = WeatherMessageModel(
                                role = response.role ?: "assistant",
                                content = response.content?.firstOrNull()?.text.orEmpty(),
                                weather = weather
                            )

                            _state.update {
                                it.copy(
                                    chatMessages = _state.value.chatMessages + claudeMessage,
                                )
                            }
                        },
                        onFailure = { throwable ->

                        }
                    )
                }

            } catch (e: Exception) {
                logger.e(e) { "❌ ViewModel: uncaught login exception" }
                onEffect(ChatEffect.ShowError(e.message ?: "Ошибка авторизации"))
            } finally {
            }
        }
    }
}
