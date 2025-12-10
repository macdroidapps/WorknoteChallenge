package ru.macdroid.worknote.features.chat.domain.utils

import ru.macdroid.worknote.features.chat.domain.utils.format

/**
 * Утилита для приблизительной оценки количества токенов
 *
 * Примечание: Это приблизительная оценка. Точный подсчёт делает сервер.
 * Используется для предварительной проверки и UI индикации.
 */
object TokenEstimator {

    /**
     * Приблизительная оценка токенов для текста
     * Правило: ~1 токен на 4 символа для английского
     * Для русского: ~1 токен на 2-3 символа (кириллица кодируется сложнее)
     */
    fun estimateTokens(text: String): Int {
        if (text.isEmpty()) return 0

        val cyrillicCount = text.count { it.code in 0x0400..0x04FF }
        val totalChars = text.length
        val latinCount = totalChars - cyrillicCount

        // Формула: латиница /4 + кириллица /2.5
        val latinTokens = latinCount / 4
        val cyrillicTokens = (cyrillicCount * 10) / 25 // /2.5

        return (latinTokens + cyrillicTokens).coerceAtLeast(1)
    }

    /**
     * Оценка токенов для списка сообщений
     */
    fun estimateTokensForMessages(messages: List<String>): Int {
        // Добавляем накладные расходы на форматирование JSON
        val contentTokens = messages.sumOf { estimateTokens(it) }
        val overheadPerMessage = 4 // примерно для role, content и разделителей
        return contentTokens + (messages.size * overheadPerMessage)
    }

    /**
     * Форматирование количества токенов для отображения
     */
    fun formatTokenCount(tokens: Int): String {
        return when {
            tokens < 1000 -> "$tokens"
            tokens < 1_000_000 -> "${tokens / 1000}K"
            else -> "${tokens / 1_000_000}M"
        }
    }

    /**
     * Проверка, не превышает ли текст лимит
     */
    fun isWithinLimit(text: String, maxTokens: Int): Boolean {
        return estimateTokens(text) <= maxTokens
    }

    /**
     * Получить цвет индикатора в зависимости от использования лимита
     */
    fun getUsageLevel(currentTokens: Int, maxTokens: Int): TokenUsageLevel {
        val percentage = (currentTokens.toFloat() / maxTokens) * 100
        return when {
            percentage < 50 -> TokenUsageLevel.LOW
            percentage < 75 -> TokenUsageLevel.MEDIUM
            percentage < 90 -> TokenUsageLevel.HIGH
            else -> TokenUsageLevel.CRITICAL
        }
    }

    /**
     * Приблизительная стоимость запроса
     */
    fun estimateCost(
        inputTokens: Int,
        outputTokens: Int,
        costPerInputToken: Double,
        costPerOutputToken: Double
    ): Double {
        return (inputTokens * costPerInputToken) + (outputTokens * costPerOutputToken)
    }

    /**
     * Форматирование стоимости для отображения
     */
    fun formatCost(cost: Double): String {
        return when {
            cost < 0.01 -> {
                val formatted = "%.4f".format(cost)
                "$formatted¢"
            }
            cost < 1.0 -> {
                val formatted = "%.2f".format(cost)
                "$formatted¢"
            }
            else -> {
                val formatted = "%.2f".format(cost)
                "$$formatted"
            }
        }
    }
}

/**
 * Уровень использования токенов
 */
enum class TokenUsageLevel {
    LOW,      // < 50%
    MEDIUM,   // 50-75%
    HIGH,     // 75-90%
    CRITICAL  // > 90%
}

/**
 * Результат анализа токенов
 */
data class TokenAnalysis(
    val estimatedInputTokens: Int,
    val estimatedOutputTokens: Int,
    val estimatedTotalTokens: Int,
    val maxInputTokens: Int,
    val maxOutputTokens: Int,
    val maxTotalTokens: Int,
    val usageLevel: TokenUsageLevel,
    val isWithinLimits: Boolean,
    val estimatedCost: Double,
    val warning: String? = null
) {
    companion object {
        fun analyze(
            inputText: String,
            conversationHistory: List<String>,
            maxInputTokens: Int,
            maxOutputTokens: Int,
            maxTotalTokens: Int,
            costPerInputToken: Double,
            costPerOutputToken: Double,
            expectedOutputTokens: Int = 1024
        ): TokenAnalysis {
            val inputTokens = TokenEstimator.estimateTokensForMessages(
                conversationHistory + inputText
            )
            val outputTokens = expectedOutputTokens
            val totalTokens = inputTokens + outputTokens

            val usageLevel = TokenEstimator.getUsageLevel(inputTokens, maxInputTokens)
            val isWithinLimits = inputTokens <= maxInputTokens &&
                               totalTokens <= maxTotalTokens

            val cost = TokenEstimator.estimateCost(
                inputTokens,
                outputTokens,
                costPerInputToken,
                costPerOutputToken
            )

            val warning = when {
                inputTokens > maxInputTokens ->
                    "⚠️ Превышен лимит входных токенов: $inputTokens > $maxInputTokens"
                totalTokens > maxTotalTokens ->
                    "⚠️ Превышен общий лимит токенов: $totalTokens > $maxTotalTokens"
                usageLevel == TokenUsageLevel.CRITICAL ->
                    "⚠️ Критическое использование лимита: ${(inputTokens.toFloat() / maxInputTokens * 100).toInt()}%"
                usageLevel == TokenUsageLevel.HIGH ->
                    "⚡ Высокое использование лимита: ${(inputTokens.toFloat() / maxInputTokens * 100).toInt()}%"
                else -> null
            }

            return TokenAnalysis(
                estimatedInputTokens = inputTokens,
                estimatedOutputTokens = outputTokens,
                estimatedTotalTokens = totalTokens,
                maxInputTokens = maxInputTokens,
                maxOutputTokens = maxOutputTokens,
                maxTotalTokens = maxTotalTokens,
                usageLevel = usageLevel,
                isWithinLimits = isWithinLimits,
                estimatedCost = cost,
                warning = warning
            )
        }
    }
}

