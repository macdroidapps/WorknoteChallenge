package ru.macdroid.worknote.features.s05_e01_chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeRequestDTO(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val messages: List<MessageDTO>
)

@Serializable
data class MessageDTO(
    val role: String,
    val content: String
)