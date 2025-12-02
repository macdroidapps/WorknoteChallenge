package ru.macdroid.worknote.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual class AppContext internal constructor(val context: Context)

actual object AppContextProvider {
    @Composable
    actual fun current(): AppContext = AppContext(LocalContext.current)
}

