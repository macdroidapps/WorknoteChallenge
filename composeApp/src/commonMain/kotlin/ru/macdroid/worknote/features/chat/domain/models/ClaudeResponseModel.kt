package ru.macdroid.worknote.features.chat.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ClaudeResponseModel(
    val content: List<ResponseContent>?,
    val id: String?,
    val model: String?,
    val role: String?,
    val stop_reason: String?,
    val stop_sequence: String?,
    val type: String?,
    val usage: Usage?
)

@Serializable
data class ResponseContent(
    val text: String?,
    val type: String?
)

@Serializable
data class Usage(
    val cache_creation: CacheCreation?,
    val cache_creation_input_tokens: Int?,
    val cache_read_input_tokens: Int?,
    val input_tokens: Int?,
    val output_tokens: Int?,
    val service_tier: String?
)

@Serializable
data class CacheCreation(
    val ephemeral_1h_input_tokens: Int?,
    val ephemeral_5m_input_tokens: Int?
)