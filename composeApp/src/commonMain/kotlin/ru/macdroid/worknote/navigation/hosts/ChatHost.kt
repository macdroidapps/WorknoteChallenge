package ru.macdroid.worknote.navigation.hosts

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatEffect
import ru.macdroid.worknote.features.s05_e01_chat.presentation.ChatRoot
import ru.macdroid.worknote.features.s05_e01_chat.presentation.ChatViewModel
import ru.macdroid.worknote.navigation.graphModels.ChatHost
import ru.macdroid.worknote.utils.ObserveAsEvents

@Composable
fun ChatHost(onLoginSuccess: () -> Unit) {
    val backStack = remember { mutableStateListOf<ChatHost>(ChatHost.Chat) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ChatHost.Chat> {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val viewModel: ChatViewModel = koinInject()

                val state by viewModel.state.collectAsStateWithLifecycle()
                val events = viewModel.effects
                ObserveAsEvents(events) { effect ->
                    when (effect) {
                        is ChatEffect.NavigateNext -> {
                            onLoginSuccess()
                        }

                        is ChatEffect.ShowError -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(effect.message)
                            }
                        }
                    }
                }
                ChatRoot(
                    snackbarHostState = snackbarHostState,
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    )
}