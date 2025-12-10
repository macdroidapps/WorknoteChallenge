package ru.macdroid.worknote.features.chat.domain.utils

import kotlin.math.pow
import kotlin.math.round

/**
 * Форматирование Double в строку с заданным количеством знаков после запятой
 */
fun Double.formatDecimal(decimals: Int): String {
    val multiplier = 10.0.pow(decimals)
    val rounded = round(this * multiplier) / multiplier

    return if (decimals == 0) {
        rounded.toInt().toString()
    } else {
        // Конвертируем в строку с нужным количеством знаков
        val intPart = rounded.toInt()
        val fracPart = ((rounded - intPart) * multiplier).toInt()
        val fracStr = fracPart.toString().padStart(decimals, '0')
        "$intPart.$fracStr"
    }
}

/**
 * Extension для String.format совместимая с KMP
 */
fun String.format(vararg args: Any?): String {
    var result = this
    args.forEachIndexed { index, arg ->
        val placeholder = when {
            this.contains("%.4f") -> "%.4f"
            this.contains("%.2f") -> "%.2f"
            this.contains("%d") -> "%d"
            this.contains("%s") -> "%s"
            else -> "%${index + 1}\$s"
        }

        val value = when (arg) {
            is Double -> {
                when {
                    placeholder == "%.4f" -> arg.formatDecimal(4)
                    placeholder == "%.2f" -> arg.formatDecimal(2)
                    else -> arg.toString()
                }
            }
            is Float -> {
                when {
                    placeholder == "%.4f" -> arg.toDouble().formatDecimal(4)
                    placeholder == "%.2f" -> arg.toDouble().formatDecimal(2)
                    else -> arg.toString()
                }
            }
            else -> arg.toString()
        }

        result = result.replaceFirst(placeholder, value)
    }
    return result
}

