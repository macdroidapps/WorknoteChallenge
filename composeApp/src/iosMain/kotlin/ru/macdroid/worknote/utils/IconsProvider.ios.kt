package ru.macdroid.worknote.utils

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

/**
 * iOS-specific implementation: simple Text-based icon (emoji) for shared code.
 */
actual object IconsProvider {
    @Composable
    actual fun icons(): Icons = Icons()
}

actual class Icons {
    @Composable
    actual fun UserIcon() {
        Text("👤")
    }

    @Composable
    actual fun CloseIcon() {
        Text("✕")
    }

    @Composable
    actual fun VisibilityButton() {
        Text("👁️")
    }

    @Composable
    actual fun PasswordIcon() {
        Text("🔒")
    }
}

