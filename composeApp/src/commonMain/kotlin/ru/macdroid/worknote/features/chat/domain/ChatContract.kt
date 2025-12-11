package ru.macdroid.worknote.features.chat.domain

import ru.macdroid.worknote.features.chat.domain.models.AiModel
import ru.macdroid.worknote.features.chat.domain.models.CompressionStats
import ru.macdroid.worknote.features.chat.domain.models.MessageModel
import ru.macdroid.worknote.features.chat.domain.utils.TokenAnalysis


data class ChatState(
    val chatMessages: List<MessageModel> = emptyList(),
    val isLoading: Boolean = false,
    val currentMessage: String? = null,
    val userName: String? = null,
    val selectedModel: AiModel = AiModel.DEEPSEEK,
    val lastResponseTimeMs: Long? = null,
    val lastInputTokens: Int? = null,
    val lastOutputTokens: Int? = null,
    val lastTotalTokens: Int? = null,
    val lastEstimatedCost: Double? = null,
    val showTokenTestPanel: Boolean = false,
    val currentTokenAnalysis: TokenAnalysis? = null,
    val totalSessionInputTokens: Int = 0,
    val totalSessionOutputTokens: Int = 0,
    val totalSessionCost: Double = 0.0,
    val compressionStats: CompressionStats? = null
)

sealed class ChatEvent {
    data class SendMessageToChat(val message: String) : ChatEvent()
    data class UpdateCurrentMessage(val message: String) : ChatEvent()
    data class SetUserName(val name: String) : ChatEvent()
    data class SelectModel(val model: AiModel) : ChatEvent()
    data object ClearChat : ChatEvent()
    data object ToggleTokenTestPanel : ChatEvent()
    data class SendTestMessage(val testCaseMessage: String) : ChatEvent()
}

sealed class ChatEffect {
    data object NavigateNext : ChatEffect()
    data class ShowError(val message: String) : ChatEffect()
    data class ShowTokenWarning(val warning: String) : ChatEffect()
}

