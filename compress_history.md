# Dialogue Compression System Implementation Guide

## Overview
This guide demonstrates how to implement dialogue compression in Android applications using Kotlin. The system automatically summarizes conversation history to reduce token usage while maintaining context quality.

## Core Concepts

### How Neural Networks Compress Dialogues

Dialogue compression transforms long conversation histories into concise summaries that preserve key context.

**Main Approaches:**
1. **Extractive summarization** - Selecting the most important messages
2. **Abstractive summarization** - Generating new text that captures the essence
3. **Hybrid approach** - Combining both methods

**What Gets Preserved:**
- Key facts and data
- Current task context
- Important user decisions
- System/application state

**What Gets Removed:**
- Greetings and formalities
- Repetitive information
- Outdated context
- Completed intermediate steps

## Implementation

### 1. Data Models
```kotlin
// domain/model/Message.kt
data class Message(
    val id: String,
    val role: Role,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: MessageMetadata? = null
) {
    enum class Role {
        USER, ASSISTANT, SYSTEM
    }
}

data class MessageMetadata(
    val isImportant: Boolean = false,
    val category: MessageCategory? = null,
    val relatedMessageIds: List<String> = emptyList()
)

enum class MessageCategory {
    GREETING,
    QUESTION,
    ANSWER,
    TASK_DEFINITION,
    DECISION,
    ERROR,
    RESULT
}

data class DialogueSummary(
    val id: String,
    val summaryText: String,
    val originalMessageCount: Int,
    val compressedMessageIds: List<String>,
    val keyPoints: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val tokensOriginal: Int,
    val tokensSummary: Int
)

data class ConversationState(
    val messages: List<Message>,
    val summaries: List<DialogueSummary>,
    val activeMessageCount: Int
)
```

### 2. Compression Configuration
```kotlin
// domain/model/CompressionConfig.kt
data class CompressionConfig(
    val compressionThreshold: Int = 10,        // Compress every N messages
    val keepRecentMessages: Int = 3,           // Keep last N messages uncompressed
    val maxTokensBeforeCompression: Int = 8000,
    val compressionRatio: Float = 0.3f         // Target summary size (30% of original)
)
```

### 3. Compression Service
```kotlin
// domain/usecase/DialogueCompressionUseCase.kt
interface DialogueCompressionService {
    suspend fun compressMessages(
        messages: List<Message>,
        existingSummaries: List<DialogueSummary> = emptyList()
    ): DialogueSummary
    
    suspend fun shouldCompress(
        messageCount: Int,
        tokenCount: Int,
        config: CompressionConfig
    ): Boolean
}

class DialogueCompressionUseCaseImpl(
    private val aiService: AIService,
    private val tokenCounter: TokenCounter,
    private val config: CompressionConfig
) : DialogueCompressionService {
    
    override suspend fun compressMessages(
        messages: List<Message>,
        existingSummaries: List<DialogueSummary>
    ): DialogueSummary {
        val messagesToCompress = messages.filterNot { it.isRecent() }
        
        val compressionPrompt = buildCompressionPrompt(
            messages = messagesToCompress,
            existingSummaries = existingSummaries
        )
        
        val summaryText = aiService.generateSummary(compressionPrompt)
        val keyPoints = extractKeyPoints(summaryText)
        
        val originalTokens = messagesToCompress.sumOf { 
            tokenCounter.count(it.content) 
        }
        val summaryTokens = tokenCounter.count(summaryText)
        
        return DialogueSummary(
            id = generateId(),
            summaryText = summaryText,
            originalMessageCount = messagesToCompress.size,
            compressedMessageIds = messagesToCompress.map { it.id },
            keyPoints = keyPoints,
            tokensOriginal = originalTokens,
            tokensSummary = summaryTokens
        )
    }
    
    override suspend fun shouldCompress(
        messageCount: Int,
        tokenCount: Int,
        config: CompressionConfig
    ): Boolean {
        return messageCount >= config.compressionThreshold ||
               tokenCount >= config.maxTokensBeforeCompression
    }
    
    private fun buildCompressionPrompt(
        messages: List<Message>,
        existingSummaries: List<DialogueSummary>
    ): String {
        return buildString {
            appendLine("Create a concise summary of the following dialogue.")
            appendLine("Preserve all important facts, decisions, and current context.")
            appendLine("Remove greetings, repetitions, and unimportant details.")
            appendLine()
            
            if (existingSummaries.isNotEmpty()) {
                appendLine("## Previous Context:")
                existingSummaries.forEach { summary ->
                    appendLine(summary.summaryText)
                    appendLine()
                }
            }
            
            appendLine("## Dialogue to Compress:")
            messages.forEach { message ->
                appendLine("${message.role.name}: ${message.content}")
            }
            
            appendLine()
            appendLine("## Summary Format:")
            appendLine("Write a compressed summary in this format:")
            appendLine("- Key facts: ...")
            appendLine("- Current task: ...")
            appendLine("- Important decisions: ...")
            appendLine("- Status: ...")
        }
    }
    
    private fun extractKeyPoints(summaryText: String): List<String> {
        return summaryText.lines()
            .filter { it.trim().startsWith("-") || it.trim().startsWith("•") }
            .map { it.removePrefix("-").removePrefix("•").trim() }
    }
    
    private fun Message.isRecent(): Boolean {
        val hoursSinceCreation = (System.currentTimeMillis() - timestamp) / (1000 * 60 * 60)
        return hoursSinceCreation < 1
    }
    
    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}
```

### 4. Conversation Repository with Compression
```kotlin
// domain/repository/ConversationRepository.kt
interface ConversationRepository {
    suspend fun addMessage(message: Message)
    suspend fun getActiveConversation(): ConversationState
    suspend fun compressIfNeeded()
    suspend fun getContextForAI(): String
}

class ConversationRepositoryImpl(
    private val compressionService: DialogueCompressionService,
    private val config: CompressionConfig,
    private val tokenCounter: TokenCounter
) : ConversationRepository {
    
    private val messages = mutableListOf<Message>()
    private val summaries = mutableListOf<DialogueSummary>()
    
    override suspend fun addMessage(message: Message) {
        messages.add(message)
        compressIfNeeded()
    }
    
    override suspend fun getActiveConversation(): ConversationState {
        return ConversationState(
            messages = messages.toList(),
            summaries = summaries.toList(),
            activeMessageCount = messages.size
        )
    }
    
    override suspend fun compressIfNeeded() {
        val activeMessages = getMessagesForCompression()
        val totalTokens = activeMessages.sumOf { tokenCounter.count(it.content) }
        
        val shouldCompress = compressionService.shouldCompress(
            messageCount = activeMessages.size,
            tokenCount = totalTokens,
            config = config
        )
        
        if (shouldCompress) {
            performCompression()
        }
    }
    
    private suspend fun performCompression() {
        val messagesToCompress = messages.dropLast(config.keepRecentMessages)
        
        if (messagesToCompress.isEmpty()) return
        
        val summary = compressionService.compressMessages(
            messages = messagesToCompress,
            existingSummaries = summaries
        )
        
        messages.removeAll { message ->
            message.id in summary.compressedMessageIds
        }
        
        summaries.add(summary)
        
        logCompressionStats(summary)
    }
    
    private fun getMessagesForCompression(): List<Message> {
        return messages.dropLast(config.keepRecentMessages)
    }
    
    override suspend fun getContextForAI(): String {
        return buildString {
            summaries.forEach { summary ->
                appendLine("=== Previous Context (Compressed) ===")
                appendLine(summary.summaryText)
                appendLine()
            }
            
            if (messages.isNotEmpty()) {
                appendLine("=== Current Dialogue ===")
                messages.forEach { message ->
                    appendLine("${message.role.name}: ${message.content}")
                }
            }
        }
    }
    
    private fun logCompressionStats(summary: DialogueSummary) {
        val compressionRatio = (summary.tokensSummary.toFloat() / summary.tokensOriginal * 100)
        val tokensSaved = summary.tokensOriginal - summary.tokensSummary
        
        println("""
            |Compression completed:
            |  Messages compressed: ${summary.originalMessageCount}
            |  Tokens before: ${summary.tokensOriginal}
            |  Tokens after: ${summary.tokensSummary}
            |  Tokens saved: $tokensSaved
            |  Compression ratio: ${compressionRatio.toInt()}%
        """.trimMargin())
    }
}
```

### 5. Token Counter
```kotlin
// infrastructure/TokenCounter.kt
interface TokenCounter {
    fun count(text: String): Int
}

class SimpleTokenCounter : TokenCounter {
    override fun count(text: String): Int {
        // Simplified counting: ~1 token per 4 characters (approximate)
        // For accurate counting, use tiktoken or similar
        return (text.length / 4.0).toInt().coerceAtLeast(1)
    }
}

class TikTokenCounter : TokenCounter {
    // Integration with tiktoken via JNI or API
    override fun count(text: String): Int {
        // Real GPT token counting implementation
        TODO("Implement using tiktoken library")
    }
}
```

### 6. AI Service
```kotlin
// data/api/AIService.kt
interface AIService {
    suspend fun generateSummary(prompt: String): String
    suspend fun chat(context: String, userMessage: String): String
}

class OpenAIService(
    private val apiKey: String,
    private val httpClient: HttpClient
) : AIService {
    
    private val baseUrl = "https://api.openai.com/v1"
    
    override suspend fun generateSummary(prompt: String): String {
        return chat(
            context = "You are an assistant specialized in compressing dialogues. " +
                     "Create concise, informative summaries.",
            userMessage = prompt
        )
    }
    
    override suspend fun chat(context: String, userMessage: String): String {
        val requestBody = buildJsonObject {
            put("model", "gpt-4")
            put("messages", buildJsonArray {
                add(buildJsonObject {
                    put("role", "system")
                    put("content", context)
                })
                add(buildJsonObject {
                    put("role", "user")
                    put("content", userMessage)
                })
            })
            put("max_tokens", 1000)
            put("temperature", 0.3)
        }
        
        val response: HttpResponse = httpClient.post("$baseUrl/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }
        
        val jsonResponse = Json.parseToJsonElement(response.bodyAsText())
        return jsonResponse
            .jsonObject["choices"]
            ?.jsonArray?.get(0)
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.content
            ?: throw IllegalStateException("Invalid API response")
    }
}
```

### 7. ViewModel
```kotlin
// presentation/ChatViewModel.kt
class ChatViewModel(
    private val conversationRepository: ConversationRepository,
    private val aiService: AIService
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    private val _compressionStats = MutableStateFlow<CompressionStats?>(null)
    val compressionStats: StateFlow<CompressionStats?> = _compressionStats.asStateFlow()
    
    fun sendMessage(content: String) {
        viewModelScope.launch {
            val userMessage = Message(
                id = generateId(),
                role = Message.Role.USER,
                content = content
            )
            conversationRepository.addMessage(userMessage)
            updateMessages()
            
            val context = conversationRepository.getContextForAI()
            
            val aiResponse = aiService.chat(
                context = context,
                userMessage = content
            )
            
            val assistantMessage = Message(
                id = generateId(),
                role = Message.Role.ASSISTANT,
                content = aiResponse
            )
            conversationRepository.addMessage(assistantMessage)
            updateMessages()
            
            updateCompressionStats()
        }
    }
    
    private suspend fun updateMessages() {
        val state = conversationRepository.getActiveConversation()
        _messages.value = state.messages
    }
    
    private suspend fun updateCompressionStats() {
        val state = conversationRepository.getActiveConversation()
        val totalOriginalTokens = state.summaries.sumOf { it.tokensOriginal }
        val totalCompressedTokens = state.summaries.sumOf { it.tokensSummary }
        val tokensSaved = totalOriginalTokens - totalCompressedTokens
        
        _compressionStats.value = CompressionStats(
            totalSummaries = state.summaries.size,
            totalMessagesSummarized = state.summaries.sumOf { it.originalMessageCount },
            tokensSaved = tokensSaved,
            compressionRatio = if (totalOriginalTokens > 0) {
                (totalCompressedTokens.toFloat() / totalOriginalTokens * 100).toInt()
            } else 0
        )
    }
    
    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}

data class CompressionStats(
    val totalSummaries: Int,
    val totalMessagesSummarized: Int,
    val tokensSaved: Int,
    val compressionRatio: Int
)
```

### 8. UI Components
```kotlin
// presentation/ChatScreen.kt
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val compressionStats by viewModel.compressionStats.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        compressionStats?.let { stats ->
            CompressionStatsCard(stats)
        }
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageItem(message)
            }
        }
        
        MessageInput(
            onSendMessage = { content ->
                viewModel.sendMessage(content)
            }
        )
    }
}

@Composable
fun CompressionStatsCard(stats: CompressionStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Compression Statistics",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Compressions:", stats.totalSummaries.toString())
                StatItem("Messages:", stats.totalMessagesSummarized.toString())
                StatItem("Tokens Saved:", stats.tokensSaved.toString())
                StatItem("Ratio:", "${stats.compressionRatio}%")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
```

### 9. Dependency Injection
```kotlin
// di/CompressionModule.kt
@Module
@InstallIn(SingletonComponent::class)
object CompressionModule {
    
    @Provides
    @Singleton
    fun provideCompressionConfig(): CompressionConfig {
        return CompressionConfig(
            compressionThreshold = 10,
            keepRecentMessages = 3,
            maxTokensBeforeCompression = 8000,
            compressionRatio = 0.3f
        )
    }
    
    @Provides
    @Singleton
    fun provideTokenCounter(): TokenCounter {
        return SimpleTokenCounter()
    }
    
    @Provides
    @Singleton
    fun provideCompressionService(
        aiService: AIService,
        tokenCounter: TokenCounter,
        config: CompressionConfig
    ): DialogueCompressionService {
        return DialogueCompressionUseCaseImpl(aiService, tokenCounter, config)
    }
    
    @Provides
    @Singleton
    fun provideConversationRepository(
        compressionService: DialogueCompressionService,
        config: CompressionConfig,
        tokenCounter: TokenCounter
    ): ConversationRepository {
        return ConversationRepositoryImpl(compressionService, config, tokenCounter)
    }
}
```

## Results and Metrics

### Benefits of Compression
- ✅ 60-70% reduction in token usage
- ✅ Lower API costs
- ✅ Faster responses (less context to process)
- ✅ Support for longer conversations

### Potential Issues
- ⚠️ Possible loss of non-critical details
- ⚠️ Additional latency for summary generation
- ⚠️ Requires thorough quality testing

### Recommended Metrics
- Tokens before/after compression
- Summary generation time
- Response quality (via A/B testing)
- Successful dialogue completion rate

## Testing Strategy
```kotlin
// Test compression quality
class CompressionQualityTest {
    @Test
    fun `verify key information preserved`() = runTest {
        val messages = createTestMessages()
        val summary = compressionService.compressMessages(messages)
        
        // Verify key facts are present
        assertTrue(summary.summaryText.contains("important fact"))
        assertTrue(summary.keyPoints.isNotEmpty())
    }
    
    @Test
    fun `verify compression ratio`() = runTest {
        val messages = createTestMessages()
        val summary = compressionService.compressMessages(messages)
        
        val ratio = summary.tokensSummary.toFloat() / summary.tokensOriginal
        assertTrue(ratio < 0.4f) // Should be under 40%
    }
}
```

This implementation provides an efficient dialogue compression system that significantly reduces token usage while maintaining conversation quality for Android applications.