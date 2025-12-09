package ru.macdroid.worknote.features.chat.domain.models

data class HuggingFaceResponseModel(
    val content: String,
    val model: String?,
    val inputTokens: Int?,
    val outputTokens: Int?,
    val totalTokens: Int?
)

