package ru.macdroid.worknote.features.chat.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.macdroid.worknote.features.chat.domain.models.ModelTokenLimits
import ru.macdroid.worknote.features.chat.domain.models.TokenTestCase
import ru.macdroid.worknote.features.chat.domain.utils.TokenAnalysis
import ru.macdroid.worknote.features.chat.domain.utils.TokenEstimator
import ru.macdroid.worknote.features.chat.domain.utils.TokenUsageLevel

@Composable
fun TokenTestPanel(
    isVisible: Boolean,
    currentAnalysis: TokenAnalysis?,
    limits: ModelTokenLimits,
    onTestCaseSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧪 Панель тестирования токенов",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onClose) {
                        Text("✕", style = MaterialTheme.typography.titleLarge)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Текущий анализ
                if (currentAnalysis != null) {
                    CurrentAnalysisCard(currentAnalysis, limits)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Список тестовых случаев
                Text(
                    text = "Выберите тестовый запрос:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TokenTestCase.all()) { testCase ->
                        TestCaseCard(
                            testCase = testCase,
                            limits = limits,
                            onClick = { onTestCaseSelected(testCase.messageTemplate) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentAnalysisCard(
    analysis: TokenAnalysis,
    limits: ModelTokenLimits
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getColorForUsageLevel(analysis.usageLevel)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "📊 Текущий анализ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Прогресс бар использования токенов
            val usage = analysis.estimatedInputTokens.toFloat() / limits.maxInputTokens
            LinearProgressIndicator(
                progress = { usage },
                modifier = Modifier.fillMaxWidth(),
                color = when (analysis.usageLevel) {
                    TokenUsageLevel.LOW -> Color(0xFF4CAF50)
                    TokenUsageLevel.MEDIUM -> Color(0xFFFFC107)
                    TokenUsageLevel.HIGH -> Color(0xFFFF9800)
                    TokenUsageLevel.CRITICAL -> Color(0xFFF44336)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(
                    label = "Входные",
                    value = "${analysis.estimatedInputTokens} / ${limits.maxInputTokens}",
                    emoji = "📥"
                )
                MetricColumn(
                    label = "Выходные",
                    value = "${analysis.estimatedOutputTokens}",
                    emoji = "📤"
                )
                MetricColumn(
                    label = "Стоимость",
                    value = TokenEstimator.formatCost(analysis.estimatedCost),
                    emoji = "💰"
                )
            }

            if (analysis.warning != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = analysis.warning,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TestCaseCard(
    testCase: TokenTestCase,
    limits: ModelTokenLimits,
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = testCase.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = testCase.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Индикатор лимита
                val isWithinLimit = testCase.estimatedTokens <= limits.maxInputTokens
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isWithinLimit)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isWithinLimit) "✓" else "⚠",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Предпросмотр:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = testCase.messageTemplate.take(200) +
                                   if (testCase.messageTemplate.length > 200) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val isWithinLimit = testCase.estimatedTokens <= limits.maxInputTokens
                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWithinLimit)
                                MaterialTheme.colorScheme.primary
                            else
                                Color(0xFFFF9800)
                        )
                    ) {
                        Text(
                            text = if (isWithinLimit)
                                "📤 Отправить тестовый запрос"
                            else
                                "⚠️ Отправить (превышает лимит)"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun getColorForUsageLevel(level: TokenUsageLevel): Color {
    return when (level) {
        TokenUsageLevel.LOW -> Color(0xFFE8F5E9)
        TokenUsageLevel.MEDIUM -> Color(0xFFFFF9C4)
        TokenUsageLevel.HIGH -> Color(0xFFFFE0B2)
        TokenUsageLevel.CRITICAL -> Color(0xFFFFCDD2)
    }
}

