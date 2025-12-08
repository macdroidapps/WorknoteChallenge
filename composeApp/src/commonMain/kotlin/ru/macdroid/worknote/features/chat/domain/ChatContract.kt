package ru.macdroid.worknote.features.chat.domain

import ru.macdroid.worknote.features.chat.domain.models.MessageModel


data class ChatState(
    val chatMessages: List<MessageModel> = emptyList(),
    val isLoading: Boolean = false,
    val currentMessage: String? = null,
    val userName: String? = null,
    val temperature: Float = 1.0f
)

sealed class ChatEvent {
    data class SendMessageToChat(val message: String) : ChatEvent()
    data class UpdateCurrentMessage(val message: String) : ChatEvent()
    data class SetUserName(val name: String) : ChatEvent()
    data class SetTemperature(val temperature: Float) : ChatEvent()
    data object ClearChat : ChatEvent()
}

sealed class ChatEffect {
    data object NavigateNext : ChatEffect()
    data class ShowError(val message: String) : ChatEffect()
}

