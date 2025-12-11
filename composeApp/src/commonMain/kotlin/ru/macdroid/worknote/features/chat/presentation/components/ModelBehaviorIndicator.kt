package ru.macdroid.worknote.features.chat.presentation.components

import androidx.compose.animation.AnimatedVisibility
import ru.macdroid.worknote.features.chat.domain.utils.format
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.macdroid.worknote.features.chat.domain.utils.TokenAnalysis

/**
 * Компонент для визуализации ИЗМЕНЕНИЯ ПОВЕДЕНИЯ модели
 * в зависимости от размера запроса
 */
@Composable
fun ModelBehaviorIndicator(
    analysis: TokenAnalysis?
) {
    AnimatedVisibility(visible = true) {
        analysis?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🔄 Прогноз поведения модели",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Прогноз времени ответа
                    val estimatedTimeMs = estimateResponseTime(it.estimatedInputTokens, it.estimatedOutputTokens)
                    BehaviorMetric(
                        emoji = "⏱️",
                        label = "Ожидаемое время",
                        value = formatTime(estimatedTimeMs),
                        color = getTimeColor(estimatedTimeMs)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Прогноз качества
                    val qualityLevel = estimateQualityLevel(it.estimatedInputTokens)
                    BehaviorMetric(
                        emoji = "📝",
                        label = "Качество ответа",
                        value = qualityLevel.description,
                        color = qualityLevel.color
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Статус лимита
                    val limitStatus = when {
                        !it.isWithinLimits -> LimitStatus.EXCEEDED
                        it.estimatedInputTokens > it.maxInputTokens * 0.9 -> LimitStatus.CRITICAL
                        it.estimatedInputTokens > it.maxInputTokens * 0.7 -> LimitStatus.HIGH
                        else -> LimitStatus.OK
                    }
                    BehaviorMetric(
                        emoji = limitStatus.emoji,
                        label = "Статус обработки",
                        value = limitStatus.description,
                        color = limitStatus.color
                    )

                    // Прогресс-бар
                    if (it.estimatedInputTokens > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val progress = (it.estimatedInputTokens.toFloat() / it.maxInputTokens).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = limitStatus.color
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% от лимита модели",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BehaviorMetric(
    emoji: String,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// Оценка времени ответа на основе токенов
private fun estimateResponseTime(inputTokens: Int, outputTokens: Int): Long {
    // Формула: базовое время + время на обработку входных + время на генерацию выходных
    val baseTime = 200L // мс
    val inputProcessingTime = inputTokens * 0.5 // ~0.5мс на токен
    val outputGenerationTime = outputTokens * 8.0 // ~8мс на токен (генерация медленнее)

    return (baseTime + inputProcessingTime + outputGenerationTime).toLong()
}

private fun formatTime(ms: Long): String {
    return when {
        ms < 1000 -> "${ms}мс"
        ms < 10000 -> {
            val seconds = ms / 1000.0
            "%.1f".format(seconds) + "сек"
        }
        else -> "${ms / 1000}сек"
    }
}

private fun getTimeColor(ms: Long): Color {
    return when {
        ms < 2000 -> Color(0xFF4CAF50) // Зелёный - быстро
        ms < 5000 -> Color(0xFFFFC107) // Жёлтый - нормально
        ms < 10000 -> Color(0xFFFF9800) // Оранжевый - медленно
        else -> Color(0xFFF44336) // Красный - очень медленно
    }
}

// Оценка качества ответа
private fun estimateQualityLevel(inputTokens: Int): QualityLevel {
    return when {
        inputTokens < 10 -> QualityLevel(
            "Минимальный",
            "Очень короткий запрос → поверхностный ответ",
            Color(0xFFF44336)
        )
        inputTokens < 50 -> QualityLevel(
            "Базовый",
            "Мало контекста → общий ответ",
            Color(0xFFFF9800)
        )
        inputTokens < 200 -> QualityLevel(
            "Хороший",
            "Достаточно контекста → качественный ответ",
            Color(0xFFFFC107)
        )
        inputTokens < 1000 -> QualityLevel(
            "Отличный",
            "Много контекста → детальный ответ",
            Color(0xFF4CAF50)
        )
        else -> QualityLevel(
            "Избыточный",
            "Слишком много контекста → возможно усечение",
            Color(0xFF9C27B0)
        )
    }
}

private data class QualityLevel(
    val name: String,
    val description: String,
    val color: Color
)

private enum class LimitStatus(
    val emoji: String,
    val description: String,
    val color: Color
) {
    OK("✅", "Будет обработан полностью", Color(0xFF4CAF50)),
    HIGH("⚡", "Высокая нагрузка, может быть медленнее", Color(0xFFFF9800)),
    CRITICAL("⚠️", "На грани лимита, возможны проблемы", Color(0xFFFF5722)),
    EXCEEDED("❌", "Превышен лимит, будет отклонён", Color(0xFFF44336))
}

