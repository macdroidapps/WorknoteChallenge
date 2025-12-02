package ru.macdroid.worknote.utils

import androidx.compose.runtime.Composable

actual class AppContext

actual object AppContextProvider {
    @Composable
    actual fun current(): AppContext = AppContext()
}
