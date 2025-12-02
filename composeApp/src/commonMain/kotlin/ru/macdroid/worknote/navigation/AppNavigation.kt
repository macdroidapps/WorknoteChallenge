package ru.macdroid.worknote.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.macdroid.worknote.navigation.graphModels.RootGraph
import ru.macdroid.worknote.navigation.hosts.ChatHost
import ru.macdroid.worknote.navigation.hosts.WeatherHost

@Composable
fun AppNavigationRoot() {
    val backStack = remember { mutableStateListOf<RootGraph>(RootGraph.Weather) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<RootGraph.Chat> {
                ChatHost(
                    onLoginSuccess = {
                        backStack.clear()
//                        backStack.add(RootGraph.Wizard)
                    }
                )
            }

            entry<RootGraph.Weather> {
                WeatherHost()
            }
        }
    )
}



