package ru.macdroid.worknote.features.chat.domain.models

/**
 * Тестовые случаи для демонстрации работы с токенами
 */
sealed class TokenTestCase(
    val displayName: String,
    val description: String,
    val estimatedTokens: Int,
    val messageTemplate: String
) {
    data object Short : TokenTestCase(
        displayName = "Короткий запрос",
        description = "~20 токенов",
        estimatedTokens = 20,
        messageTemplate = "Привет! Расскажи мне интересный факт о программировании."
    )

    data object Medium : TokenTestCase(
        displayName = "Средний запрос",
        description = "~100 токенов",
        estimatedTokens = 100,
        messageTemplate = """
            Я изучаю Kotlin Multiplatform и хочу создать кроссплатформенное приложение.
            У меня есть несколько вопросов:
            1. Какие основные преимущества KMP перед другими решениями?
            2. Как правильно структурировать проект?
            3. Какие библиотеки лучше использовать для работы с сетью?
            Пожалуйста, дай развёрнутый ответ с примерами.
        """.trimIndent()
    )

    data object Long : TokenTestCase(
        displayName = "Длинный запрос",
        description = "~500 токенов",
        estimatedTokens = 500,
        messageTemplate = """
            Мне нужна помощь в оптимизации производительности Compose Multiplatform приложения.
            
            Контекст проекта:
            - Используем Kotlin Multiplatform с общей бизнес-логикой
            - UI написан на Jetpack Compose для Android и Compose Multiplatform для iOS
            - Приложение работает с большими списками данных (более 10000 элементов)
            - Используем Room для локального хранения данных
            - Интегрированы API Claude и HuggingFace для AI-функций
            
            Проблемы, с которыми столкнулись:
            1. Медленная прокрутка длинных списков на iOS
            2. Высокое потребление памяти при работе с изображениями
            3. Задержки при переключении между экранами
            4. Периодические зависания UI при загрузке данных из API
            
            Текущая архитектура:
            - MVVM с использованием StateFlow
            - Koin для dependency injection
            - Ktor для сетевых запросов
            - Coil для загрузки изображений
            
            Вопросы:
            1. Как правильно реализовать пагинацию для LazyColumn с учётом KMP?
            2. Какие паттерны использовать для эффективного кэширования изображений?
            3. Стоит ли использовать Kotlin Coroutines Flow или лучше перейти на альтернативы?
            4. Какие инструменты профилирования рекомендуешь для iOS части?
            5. Как оптимизировать recomposition в Compose?
            
            Пожалуйста, предоставь подробные рекомендации с примерами кода и best practices.
        """.trimIndent()
    )

    data object VeryLong : TokenTestCase(
        displayName = "Очень длинный",
        description = "~1500 токенов",
        estimatedTokens = 1500,
        messageTemplate = """
            Я работаю над комплексным корпоративным приложением на Kotlin Multiplatform и столкнулся с множеством архитектурных и технических вопросов.
            
            ОБЩИЙ КОНТЕКСТ ПРОЕКТА:
            Разрабатываем кроссплатформенное мобильное приложение для управления проектами и командной работы. Целевые платформы: Android, iOS и Web (Wasm).
            
            Технологический стек:
            - Kotlin Multiplatform 2.0
            - Compose Multiplatform для UI (включая Wasm)
            - Ktor Client для сетевых запросов
            - SQLDelight для базы данных (с поддержкой кэширования)
            - Koin для Dependency Injection
            - Kotlinx.serialization для работы с JSON
            - Kotlinx.coroutines + Flow для асинхронности
            - Coil3 для загрузки и кэширования изображений
            - Material 3 для дизайна
            
            АРХИТЕКТУРА:
            - Clean Architecture с разделением на слои: data, domain, presentation
            - MVI паттерн для управления состоянием
            - Repository паттерн для работы с данными
            - UseCase для бизнес-логики
            
            ТЕКУЩИЕ ПРОБЛЕМЫ И ВОПРОСЫ:
            
            1. ПРОИЗВОДИТЕЛЬНОСТЬ:
            - LazyColumn с 10000+ элементов лагает на iOS 
            - Переключение между экранами занимает 2-3 секунды
            - Scroll performance падает при загрузке изображений
            - Memory leaks при работе с большими данными
            
            2. РАБОТА С API:
            - Интегрированы Claude API и HuggingFace для AI-функций
            - Нужна система отслеживания токенов с лимитами
            - Требуется механизм повторных попыток при ошибках сети
            - Как правильно обрабатывать streaming responses?
            - Нужен механизм кэширования ответов AI
            
            3. СИНХРОНИЗАЦИЯ ДАННЫХ:
            - Offline-first подход с синхронизацией при появлении сети
            - Конфликты при одновременном редактировании данных разными пользователями
            - Версионирование данных и миграции схемы БД
            - Как реализовать differential sync для минимизации трафика?
            
            4. БЕЗОПАСНОСТЬ:
            - Шифрование локальных данных
            - Безопасное хранение токенов и ключей API
            - Certificate pinning для защиты от MITM атак
            - Какие библиотеки использовать для криптографии в KMP?
            
            5. ТЕСТИРОВАНИЕ:
            - Unit тесты для общего кода
            - UI тесты для каждой платформы
            - Integration тесты для API
            - Как организовать моки для платформенных зависимостей?
            - Нужны ли screenshot tests для KMP?
            
            6. CI/CD:
            - Автоматическая сборка для всех платформ
            - Deployment в App Store и Google Play
            - Версионирование и changelog
            - Какие инструменты использовать для automated testing?
            
            7. СПЕЦИФИЧНЫЕ ВОПРОСЫ ПО ПЛАТФОРМАМ:
            
            Android:
            - Поддержка Android 6-14
            - Размер APK уже 50MB, как оптимизировать?
            - ProGuard/R8 правила для KMP
            
            iOS:
            - Минимальная версия iOS 14
            - Размер IPA 80MB, нужна оптимизация
            - Интеграция с native iOS features (Push Notifications, Background tasks)
            - Memory management в Swift/Kotlin interop
            
            Web/Wasm:
            - Initial load time 5 секунд - слишком медленно
            - SEO оптимизация
            - Browser compatibility
            
            8. МОНИТОРИНГ И АНАЛИТИКА:
            - Crash reporting (Firebase? Sentry?)
            - Performance monitoring
            - User analytics
            - Какие KMP-совместимые решения существуют?
            
            КОНКРЕТНЫЕ ЗАПРОСЫ:
            1. Дай подробный план оптимизации производительности с приоритетами
            2. Предложи архитектурные улучшения для работы с AI API и токенами
            3. Как реализовать эффективную систему кэширования на всех уровнях?
            4. Покажи примеры кода для критичных компонентов
            5. Какие anti-patterns мы можем использовать и как их избежать?
            6. Предложи метрики для мониторинга качества приложения
            7. Roadmap для перехода на production-ready состояние
            
            Пожалуйста, предоставь максимально подробный ответ с конкретными рекомендациями, примерами кода и ссылками на документацию.
        """.trimIndent()
    )

    data object ExtremelyLong : TokenTestCase(
        displayName = "Экстремально длинный",
        description = "~3000+ токенов (может превысить лимит)",
        estimatedTokens = 3000,
        messageTemplate = buildExtremelyLongMessage()
    )

    companion object {
        fun all() = listOf(Short, Medium, Long, VeryLong, ExtremelyLong)

        private fun buildExtremelyLongMessage(): String {
            val baseContent = VeryLong.messageTemplate
            val repetitions = """
                
                ДОПОЛНИТЕЛЬНЫЙ КОНТЕКСТ:
                ${(1..10).joinToString("\n\n") { iteration ->
                    """
                    ИТЕРАЦИЯ $iteration:
                    Повторяющийся контекст для увеличения размера запроса.
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
                    Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                    Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.
                    Duis aute irure dolor in reprehenderit in voluptate velit esse.
                    Excepteur sint occaecat cupidatat non proident, sunt in culpa qui.
                    
                    Технические детали итерации $iteration:
                    - Database queries optimization needed
                    - Network layer refactoring required
                    - UI performance improvements planned
                    - Memory management review scheduled
                    - Code quality metrics to be improved
                    - Testing coverage needs expansion
                    - Documentation requires updates
                    - Security audit pending
                    """.trimIndent()
                }}
                
                ЗАКЛЮЧЕНИЕ:
                Этот запрос специально создан очень длинным для тестирования лимитов модели.
                Он может превысить максимально допустимое количество токенов контекста.
                Ожидаемое поведение: либо усечение, либо ошибка о превышении лимита.
            """.trimIndent()

            return baseContent + repetitions
        }
    }
}

/**
 * Информация о лимитах токенов для разных моделей
 */
data class ModelTokenLimits(
    val model: AiModel,
    val maxInputTokens: Int,
    val maxOutputTokens: Int,
    val maxTotalTokens: Int,
    val costPerInputToken: Double,
    val costPerOutputToken: Double
) {
    companion object {
        fun getForModel(model: AiModel): ModelTokenLimits = when (model) {
            AiModel.DEEPSEEK -> ModelTokenLimits(
                model = model,
                maxInputTokens = 32_000,
                maxOutputTokens = 8_000,
                maxTotalTokens = 40_000,
                costPerInputToken = 0.00015,
                costPerOutputToken = 0.0006
            )
            AiModel.QWEN -> ModelTokenLimits(
                model = model,
                maxInputTokens = 32_000,
                maxOutputTokens = 8_000,
                maxTotalTokens = 40_000,
                costPerInputToken = 0.0002,
                costPerOutputToken = 0.0008
            )
            AiModel.LLAMA -> ModelTokenLimits(
                model = model,
                maxInputTokens = 8_000,
                maxOutputTokens = 4_000,
                maxTotalTokens = 12_000,
                costPerInputToken = 0.0001,
                costPerOutputToken = 0.0004
            )
        }
    }
}

