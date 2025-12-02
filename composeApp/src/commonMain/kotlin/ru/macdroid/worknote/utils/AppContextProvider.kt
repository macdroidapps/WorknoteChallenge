package ru.macdroid.worknote.utils

import androidx.compose.runtime.Composable

/**
 * Provides access to a platform-specific context object inside shared Compose code.
 */
expect object AppContextProvider {
    @Composable
    fun current(): AppContext
}

expect class AppContext

