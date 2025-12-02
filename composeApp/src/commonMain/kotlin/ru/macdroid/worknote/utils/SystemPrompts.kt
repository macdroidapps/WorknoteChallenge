package ru.macdroid.worknote.utils

fun getWeatherSystemPrompt(): String = """
        Ты API для получения информации о погоде.
        
        ВАЖНО: Твой ответ ДОЛЖЕН быть ТОЛЬКО валидным JSON объектом без дополнительного текста.
        Не добавляй объяснения, не используй markdown форматирование (```json).
        
        Формат ответа:
        {
            "city": "название города",
            "temperature": число (градусы Цельсия),
        }
        
        Пример правильного ответа:
        {"city":"Москва","temperature":25}
    """.trimIndent()