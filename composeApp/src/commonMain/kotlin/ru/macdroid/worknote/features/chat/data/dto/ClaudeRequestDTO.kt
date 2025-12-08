package ru.macdroid.worknote.features.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeRequestDTO(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val messages: List<MessageDTO>,
    val temperature: Float = 1.0f
)

@Serializable
data class MessageDTO(
    val role: String,
    val content: String
)