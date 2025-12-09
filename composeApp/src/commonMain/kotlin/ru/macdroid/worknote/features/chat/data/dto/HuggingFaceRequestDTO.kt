package ru.macdroid.worknote.features.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HuggingFaceRequestDTO(
    val model: String,
    val messages: List<HuggingFaceMessageDTO>,
    @SerialName("max_tokens")
    val maxTokens: Int = 1024,
    val stream: Boolean = false
)

@Serializable
data class HuggingFaceMessageDTO(
    val role: String,
    val content: String
)

