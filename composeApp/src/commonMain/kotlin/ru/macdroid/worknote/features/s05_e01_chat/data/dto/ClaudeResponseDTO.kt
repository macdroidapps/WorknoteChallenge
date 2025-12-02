package ru.macdroid.worknote.features.s05_e01_chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClaudeResponseDTO(
    val content: List<ResponseContentDTO>?,
    val id: String?,
    val model: String?,
    val role: String?,
    val stop_reason: String?,
    val stop_sequence: String?,
    val type: String?,
    val usage: UsageDTO?
)

@Serializable
data class ResponseContentDTO(
    val text: String?,
    val type: String?
)

@Serializable
data class UsageDTO(
    val cache_creation: CacheCreationDTO?,
    val cache_creation_input_tokens: Int?,
    val cache_read_input_tokens: Int?,
    val input_tokens: Int?,
    val output_tokens: Int?,
    val service_tier: String?
)

@Serializable
data class CacheCreationDTO(
    val ephemeral_1h_input_tokens: Int?,
    val ephemeral_5m_input_tokens: Int?
)