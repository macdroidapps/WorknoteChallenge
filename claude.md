# Документация проекта AgentChallenge (Worknote)

> **⚠️ ВАЖНО: После внесения любых изменений в проект необходимо обновлять данный файл!**

## Обзор проекта

Мультиплатформенное приложение на **Compose Multiplatform** для общения с AI-моделями (Claude, HuggingFace). Построено с использованием архитектуры **MVI (Model-View-Intent)** и принципов **Clean Architecture**.

### Поддерживаемые платформы
- **Android** (основная платформа)
- **iOS** (через Kotlin/Native)

---

## Технологический стек

### Основные технологии
| Технология | Версия | Назначение |
|------------|--------|------------|
| Kotlin | 2.2.20 | Язык программирования |
| Compose Multiplatform | 1.9.0 | UI Framework |
| Kotlin Coroutines | 1.10.2 | Асинхронность |
| Ktor | 3.3.0 | HTTP клиент |
| Room | 2.8.4 | Локальная БД |
| Koin | 4.1.1 | Dependency Injection |
| Kotlinx Serialization | 1.9.0 | Сериализация JSON |

### Дополнительные библиотеки
- **Kermit** - Логирование
- **Coil** - Загрузка изображений
- **KStore** - Хранение данных
- **Material Kolor** - Динамические цвета
- **Kotlinx DateTime** - Работа с датами

---

## Структура проекта

```
AgentChallenge/
├── build.gradle.kts              # Корневой build файл
├── settings.gradle.kts           # Настройки проекта
├── gradle/
│   └── libs.versions.toml        # Версии зависимостей (Version Catalog)
├── composeApp/                   # Основной модуль приложения
│   ├── build.gradle.kts
│   └── src/
│       ├── androidMain/          # Android-специфичный код
│       ├── iosMain/              # iOS-специфичный код
│       ├── commonMain/           # Общий код (KMP)
│       └── commonTest/           # Общие тесты
└── iosApp/                       # iOS проект (Xcode)
    └── iosApp.xcodeproj/
```

---

## Архитектура приложения (Clean Architecture + MVI)

### Структура commonMain

```
ru.macdroid.worknote/
├── App.kt                        # Точка входа Compose
├── datasources/                  # Источники данных
│   ├── di/                       # DI модули для datasources
│   │   ├── KoinApiModules.kt
│   │   └── KoinSettingsModule.kt
│   ├── local/                    # Локальные источники
│   │   ├── db/                   # Room Database
│   │   │   ├── AppDatabase.kt
│   │   │   ├── WorkNoteDatabase.kt
│   │   │   ├── OrganizationDao.kt
│   │   │   ├── OrganizationEntity.kt
│   │   │   └── getRoomDatabase.kt
│   │   ├── device/               # Устройство
│   │   └── settings/             # Настройки
│   │       └── ChatStorageImpl.kt
│   └── network/                  # Сетевые источники
│       └── CreateHttpClient.kt   # Конфигурация Ktor
├── di/                           # Глобальные DI модули
│   └── KoinLoggerModule.kt
├── features/                     # Фичи приложения
│   └── chat/                     # Фича чата с AI
│       ├── data/                 # Data слой
│       │   ├── api/              # API интерфейсы
│       │   │   ├── HuggingFaceApi.kt
│       │   │   └── WorkNoteChatApi.kt
│       │   ├── dto/              # Data Transfer Objects
│       │   │   ├── ClaudeRequestDTO.kt
│       │   │   ├── ClaudeResponseDTO.kt
│       │   │   ├── HuggingFaceRequestDTO.kt
│       │   │   └── HuggingFaceResponseDTO.kt
│       │   └── repository/       # Реализация репозиториев
│       │       └── ChatRemoteRepositoryImpl.kt
│       ├── di/                   # DI модули фичи
│       │   └── KoinChatModules.kt
│       ├── domain/               # Domain слой
│       │   ├── ChatContract.kt   # MVI контракт (State, Event, Effect)
│       │   ├── mappers/          # Маппинг DTO <-> Model
│       │   ├── models/           # Domain модели
│       │   │   ├── AiModel.kt
│       │   │   ├── ClaudeRequestModel.kt
│       │   │   ├── ClaudeResponseModel.kt
│       │   │   └── HuggingFaceResponseModel.kt
│       │   ├── repositories/     # Интерфейсы репозиториев
│       │   │   ├── ChatRemoteRepository.kt
│       │   │   └── ChatStorage.kt
│       │   └── usecases/         # Use Cases
│       │       ├── SendMessageUseCase.kt
│       │       └── SendHuggingFaceMessageUseCase.kt
│       └── presentation/         # Presentation слой
│           ├── ChatScreen.kt     # UI экрана
│           └── ChatViewModel.kt  # ViewModel (MVI)
├── navigation/                   # Навигация
│   ├── AppNavigation.kt          # Корневая навигация
│   ├── graphModels/
│   │   └── NavGraphModels.kt     # Модели графов навигации
│   └── hosts/
│       └── ChatHost.kt           # Host для чата
├── theme/                        # Тема приложения
│   ├── Color.kt
│   ├── Theme.kt
│   └── ui/
│       └── CustomCircularProgressIndicator.kt
└── utils/                        # Утилиты
    ├── AppConstants.kt           # Константы (API ключи, модели)
    ├── AppContextProvider.kt
    ├── IconsProvider.kt
    ├── ObserveAsEvents.kt
    └── SystemPrompts.kt          # Системные промпты для AI
```

---

## MVI Архитектура

### ChatContract.kt - Контракт фичи

```kotlin
// State - текущее состояние UI
data class ChatState(
    val chatMessages: List<MessageModel>,
    val isLoading: Boolean,
    val currentMessage: String?,
    val userName: String?,
    val selectedModel: AiModel,
    val lastResponseTimeMs: Long?,
    val lastInputTokens: Int?,
    val lastOutputTokens: Int?,
    val lastTotalTokens: Int?
)

// Event - пользовательские действия
sealed class ChatEvent {
    data class SendMessageToChat(val message: String) : ChatEvent()
    data class UpdateCurrentMessage(val message: String) : ChatEvent()
    data class SetUserName(val name: String) : ChatEvent()
    data class SelectModel(val model: AiModel) : ChatEvent()
    data object ClearChat : ChatEvent()
}

// Effect - одноразовые эффекты (навигация, snackbar)
sealed class ChatEffect {
    data object NavigateNext : ChatEffect()
    data class ShowError(val message: String) : ChatEffect()
}
```

---

## AI Модели

### Поддерживаемые модели

**Claude (Anthropic):**
- `claude-sonnet-4-5-20250929` (Sonnet 4.5)
- `claude-sonnet-4-20250514` (Sonnet 4)
- `claude-haiku-4-5-20251001` (Haiku 4.5)

**HuggingFace:**
- `deepseek-ai/DeepSeek-V3-0324` (DeepSeek)
- `Qwen/Qwen3-235B-A22B` (Qwen)
- `dicta-il/DictaLM-3.0-24B-Thinking:publicai` (Llama)

---

## Dependency Injection (Koin)

### Модули
- `KoinLoggerModule` - Глобальный логгер
- `KoinApiModules` - HTTP клиенты и API
- `KoinSettingsModule` - Настройки приложения
- `KoinChatModules` - Зависимости фичи чата

---

## Навигация

Используется **Navigation 3** с компонентами:
- `NavDisplay` - Отображение текущего экрана
- `BackStack` - Стек навигации
- `RootGraph` - Граф навигации

### Графы
```kotlin
sealed class RootGraph {
    data object Chat : RootGraph()  // Экран чата
}
```

---

## База данных (Room)

### Entities
- `OrganizationEntity` - Сущность организации

### DAO
- `OrganizationDao` - Data Access Object

### Databases
- `AppDatabase` - Основная БД
- `WorkNoteDatabase` - БД рабочих заметок

---

## Сетевой слой (Ktor)

### Конфигурация
- Content Negotiation (JSON)
- Logging
- Platform-specific engines:
  - Android: OkHttp
  - iOS: Darwin

### API
- `WorkNoteChatApi` - Claude API
- `HuggingFaceApi` - HuggingFace API

---

## Платформо-специфичный код

### androidMain
- Koin Android интеграция
- OkHttp HTTP engine
- Room Android runtime
- Material Icons Extended
- KStore File storage

### iosMain
- Darwin HTTP engine
- KStore File storage

---

## Сборка и запуск

### Gradle команды
```bash
# Android
./gradlew :composeApp:assembleDebug

# iOS (через Xcode или)
./gradlew :composeApp:iosSimulatorArm64MainBinaries
```

### Hot Reload
Проект поддерживает Hot Reload для быстрой разработки UI.

---

## Конфигурационные файлы

| Файл | Назначение |
|------|------------|
| `gradle/libs.versions.toml` | Version Catalog |
| `local.properties` | Локальные настройки (SDK пути) |
| `gradle.properties` | Настройки Gradle |
| `composeApp/build.gradle.kts` | Build конфигурация модуля |

---

## Рекомендации по разработке

1. **Новые фичи** добавлять в `features/` по аналогии с `chat/`
2. **Следовать MVI** - State, Event, Effect в ChatContract
3. **Clean Architecture** - data → domain → presentation
4. **DI модули** регистрировать в соответствующих Koin модулях
5. **Платформо-специфичный код** выносить в `androidMain/` или `iosMain/`

---

## История изменений

| Дата | Описание |
|------|----------|
| 2025-12-10 | Создание документации проекта |

---

> **🔄 Не забывайте обновлять этот файл после внесения изменений в проект!**

