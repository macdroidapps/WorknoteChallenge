package ru.macdroid.worknote.features.s05_e01_chat.domain

import ru.macdroid.worknote.features.s05_e01_chat.domain.models.MessageModel


data class ChatState(
    val chatMessages: List<MessageModel> = emptyList(),
    val isLoading: Boolean = false,
    val currentMessage: String? = null,
    val userName: String? = null
)

sealed class ChatEvent {
    data class SendMessageToChat(val message: String) : ChatEvent()
    data class UpdateCurrentMessage(val message: String) : ChatEvent()
    data class SetUserName(val name: String) : ChatEvent()
}

sealed class ChatEffect {
    data object NavigateNext : ChatEffect()
    data class ShowError(val message: String) : ChatEffect()
}

