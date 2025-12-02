package ru.macdroid.worknote.features.s05_e01_chat.presentation

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
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatEffect
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatEvent
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatState
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.ClaudeRequestModel
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.MessageModel
import ru.macdroid.worknote.features.s05_e01_chat.domain.usecases.SendMessageUseCase
import ru.macdroid.worknote.utils.AppConstants

class ChatViewModel(
    private val logger: Logger,
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state
        .onStart {
            logger.d { "🚀 AuthViewModel started" }
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
        }
    }

    private fun setUserName(name: String) {
        _state.update {
            it.copy(
                userName = name
            )
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

        val userMessage = MessageModel(
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
                    val request = ClaudeRequestModel(
                        model = AppConstants.MODEL_SONNET_4_5,
                        maxTokens = 1024,
                        messages = _state.value.chatMessages
                    )
                    sendMessageUseCase(message = request)
                }.collect { res ->
                    res.fold(
                        onSuccess = { response ->
                            val claudeMessage = MessageModel(
                                role = response.role ?: "assistant",
                                content = response.content?.first()?.text.orEmpty()
                            )
                            _state.update {
                                it.copy(
                                    chatMessages = _state.value.chatMessages + claudeMessage
                                )
                            }
                        },
                        onFailure = {

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
