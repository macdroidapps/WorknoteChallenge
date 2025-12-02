package ru.macdroid.worknote.utils

import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material.icons.Icons as MaterialIcons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility

/**
 * Android-specific implementation that uses Material Icons.
 */
actual object IconsProvider {
    @Composable
    actual fun icons(): Icons = Icons()
}

actual class Icons {
    @Composable
    actual fun UserIcon() {
        Icon(
            imageVector = MaterialIcons.Filled.Person,
            contentDescription = "user",
            tint = LocalContentColor.current
        )
    }

    @Composable
    actual fun CloseIcon() {
        Icon(
            imageVector = MaterialIcons.Filled.Close,
            contentDescription = "clear",
            tint = LocalContentColor.current
        )
    }

    @Composable
    actual fun VisibilityButton() {
        Icon(
            imageVector = MaterialIcons.Filled.Visibility,
            contentDescription = "visibility",
            tint = LocalContentColor.current
        )
    }

    @Composable
    actual fun PasswordIcon() {
        Icon(
            imageVector = MaterialIcons.Filled.Lock,
            contentDescription = "lock",
            tint = LocalContentColor.current
        )
    }
}

