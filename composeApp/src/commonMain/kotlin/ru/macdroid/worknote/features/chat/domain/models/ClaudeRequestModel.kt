package ru.macdroid.worknote.features.chat.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeRequestModel(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val messages: List<MessageModel>,
    val temperature: Float = 1.0f
)

@Serializable
data class MessageModel(
    val role: String,
    val content: String
)