package ru.macdroid.worknote.features.chat.domain.models

import ru.macdroid.worknote.utils.AppConstants

enum class AiModel(
    val id: String,
    val displayName: String
) {
    DEEPSEEK(AppConstants.HF_MODEL_DEEPSEEK, "DeepSeek V3"),
    QWEN(AppConstants.HF_MODEL_QWEN, "Qwen3 235B"),
    LLAMA(AppConstants.HF_MODEL_LLAMA, "DictaLm 3.0")
}

