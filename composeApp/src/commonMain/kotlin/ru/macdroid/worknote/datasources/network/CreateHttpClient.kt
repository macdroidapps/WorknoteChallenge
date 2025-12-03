package ru.macdroid.worknote.datasources.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.macdroid.worknote.utils.AppConstants.CLAUDE_API_KEY

fun createHttpClient(): HttpClient {
    return HttpClient{
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                json = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000    // 2 минуты на весь запрос
            connectTimeoutMillis = 30_000     // 30 секунд на подключение
            socketTimeoutMillis = 120_000     // 2 минуты на ожидание данных
        }
        defaultRequest {
            header("x-api-key", CLAUDE_API_KEY)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
        }
    }
}