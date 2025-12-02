package ru.macdroid.worknote


import androidx.compose.runtime.*
import ru.macdroid.worknote.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.macdroid.worknote.navigation.AppNavigationRoot

@Preview
@Composable
internal fun App() = AppTheme {
    AppNavigationRoot()
}

