package ru.macdroid.worknote.features.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HuggingFaceResponseDTO(
    val id: String? = null,
    val model: String? = null,
    @SerialName("object")
    val objectType: String? = null,
    @SerialName("system_fingerprint")
    val systemFingerprint: String? = null,
    val created: Long? = null,
    val choices: List<HuggingFaceChoiceDTO>? = null,
    val usage: HuggingFaceUsageDTO? = null
)

@Serializable
data class HuggingFaceChoiceDTO(
    val index: Int? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null,
    val message: HuggingFaceMessageDTO? = null,
    val logprobs: String? = null
)

@Serializable
data class HuggingFaceUsageDTO(
    @SerialName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerialName("completion_tokens")
    val completionTokens: Int? = null,
    @SerialName("total_tokens")
    val totalTokens: Int? = null
)

