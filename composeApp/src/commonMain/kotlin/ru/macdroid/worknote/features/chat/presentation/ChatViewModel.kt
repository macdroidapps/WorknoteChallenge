package ru.macdroid.worknote.features.chat.presentation

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
import kotlin.time.TimeSource
import ru.macdroid.worknote.features.chat.domain.ChatEffect
import ru.macdroid.worknote.features.chat.domain.ChatEvent
import ru.macdroid.worknote.features.chat.domain.ChatState
import ru.macdroid.worknote.features.chat.domain.models.AiModel
import ru.macdroid.worknote.features.chat.domain.models.MessageModel
import ru.macdroid.worknote.features.chat.domain.models.ModelTokenLimits
import ru.macdroid.worknote.features.chat.domain.utils.TokenAnalysis
import ru.macdroid.worknote.features.chat.domain.utils.TokenEstimator
import ru.macdroid.worknote.features.chat.data.api.HuggingFaceApi
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceMessageDTO
import ru.macdroid.worknote.features.chat.data.dto.HuggingFaceRequestDTO

class ChatViewModel(
    private val logger: Logger,
    private val huggingFaceApi: HuggingFaceApi,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state
        .onStart {
            logger.d { "🚀 ChatViewModel started" }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ChatState()
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

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessageToChat -> sendMessageToChat(message = event.message)
            is ChatEvent.UpdateCurrentMessage -> updateCurrentMessage(event.message)
            is ChatEvent.SetUserName -> setUserName(name = event.name)
            is ChatEvent.SelectModel -> selectModel(model = event.model)
            is ChatEvent.ClearChat -> clearChat()
            is ChatEvent.ToggleTokenTestPanel -> toggleTokenTestPanel()
            is ChatEvent.SendTestMessage -> sendMessageToChat(message = event.testCaseMessage)
        }
    }

    private fun setUserName(name: String) {
        _state.update {
            it.copy(
                userName = name
            )
        }
    }

    private fun selectModel(model: AiModel) {
        _state.update {
            it.copy(
                selectedModel = model
            )
        }
    }

    private fun clearChat() {
        _state.update {
            it.copy(
                chatMessages = emptyList(),
                lastResponseTimeMs = null,
                lastInputTokens = null,
                lastOutputTokens = null,
                lastTotalTokens = null,
                lastEstimatedCost = null,
                currentTokenAnalysis = null,
                totalSessionInputTokens = 0,
                totalSessionOutputTokens = 0,
                totalSessionCost = 0.0
            )
        }
    }

    private fun toggleTokenTestPanel() {
        _state.update {
            it.copy(showTokenTestPanel = !it.showTokenTestPanel)
        }
    }

    private fun updateCurrentMessage(message: String) {
        // Анализируем токены при вводе
        val limits = ModelTokenLimits.getForModel(_state.value.selectedModel)
        val conversationHistory = _state.value.chatMessages.map { it.content }

        val analysis = TokenAnalysis.analyze(
            inputText = message,
            conversationHistory = conversationHistory,
            maxInputTokens = limits.maxInputTokens,
            maxOutputTokens = limits.maxOutputTokens,
            maxTotalTokens = limits.maxTotalTokens,
            costPerInputToken = limits.costPerInputToken,
            costPerOutputToken = limits.costPerOutputToken
        )

        _state.update {
            it.copy(
                currentMessage = message,
                currentTokenAnalysis = analysis
            )
        }

        // Показываем предупреждение если есть
        if (analysis.warning != null) {
            viewModelScope.launch {
                _effects.send(ChatEffect.ShowTokenWarning(analysis.warning))
            }
        }
    }

    private fun sendMessageToChat(message: String) {
        if (message.isBlank()) return

        val userMessage = MessageModel(
            role = "user",
            content = message
        )

        _state.update {
            it.copy(
                chatMessages = _state.value.chatMessages + userMessage,
                currentMessage = "",
                isLoading = true,
                lastResponseTimeMs = null,
                lastInputTokens = null,
                lastOutputTokens = null,
                lastTotalTokens = null
            )
        }

        loginJob?.cancel()
        loginJob = viewModelScope.launch(coroutineHandler) {

            try {
                val timeSource = TimeSource.Monotonic
                val startMark = timeSource.markNow()

                val result = withContext(Dispatchers.IO) {
                    val model = _state.value.selectedModel
                    val request = HuggingFaceRequestDTO(
                        model = model.id,
                        messages = _state.value.chatMessages.map {
                            HuggingFaceMessageDTO(role = it.role, content = it.content)
                        },
                        maxTokens = 1000,
                        stream = false
                    )
                    logger.d { "📤 Sending request to HuggingFace: model=${model.id}" }
                    huggingFaceApi.sendMessage(request)
                }

                val responseTimeMs = startMark.elapsedNow().inWholeMilliseconds

                result.fold(
                    onSuccess = { response ->
                        val content = response.choices?.firstOrNull()?.message?.content ?: ""
                        val assistantMessage = MessageModel(
                            role = "assistant",
                            content = content
                        )

                        // Рассчитываем стоимость
                        val inputTokens = response.usage?.promptTokens ?: 0
                        val outputTokens = response.usage?.completionTokens ?: 0
                        val limits = ModelTokenLimits.getForModel(_state.value.selectedModel)
                        val cost = TokenEstimator.estimateCost(
                            inputTokens = inputTokens,
                            outputTokens = outputTokens,
                            costPerInputToken = limits.costPerInputToken,
                            costPerOutputToken = limits.costPerOutputToken
                        )

                        _state.update {
                            it.copy(
                                chatMessages = _state.value.chatMessages + assistantMessage,
                                isLoading = false,
                                lastResponseTimeMs = responseTimeMs,
                                lastInputTokens = inputTokens,
                                lastOutputTokens = outputTokens,
                                lastTotalTokens = response.usage?.totalTokens,
                                lastEstimatedCost = cost,
                                currentTokenAnalysis = null,
                                totalSessionInputTokens = it.totalSessionInputTokens + inputTokens,
                                totalSessionOutputTokens = it.totalSessionOutputTokens + outputTokens,
                                totalSessionCost = it.totalSessionCost + cost
                            )
                        }

                        logger.d {
                            "💰 Request cost: ${TokenEstimator.formatCost(cost)} " +
                            "(Input: $inputTokens, Output: $outputTokens)"
                        }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false, currentTokenAnalysis = null) }
                        onEffect(ChatEffect.ShowError(error.message ?: "Ошибка запроса"))
                    }
                )

            } catch (e: Exception) {
                logger.e(e) { "❌ ViewModel: uncaught exception" }
                _state.update { it.copy(isLoading = false) }
                onEffect(ChatEffect.ShowError(e.message ?: "Ошибка"))
            }
        }
    }
}
