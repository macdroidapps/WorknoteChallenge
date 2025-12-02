package ru.macdroid.worknote.utils

import androidx.compose.runtime.Composable

/**
 * Provides access to a platform-specific context object inside shared Compose code.
 */
expect object IconsProvider {
    @Composable
    fun icons(): Icons
}

expect class Icons {
    @Composable
    fun UserIcon() //иконка пользователя в поле с email

    @Composable
    fun PasswordIcon() //иконка замка в поле с паролем

    @Composable
    fun CloseIcon() //иконка крестика "х"

    @Composable
    fun VisibilityButton() //иконка глаза для показа пароля
}
