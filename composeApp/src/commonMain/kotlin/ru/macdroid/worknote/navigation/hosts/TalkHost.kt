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
import ru.macdroid.worknote.features.s05_e03_talking.domain.ChatEffect
import ru.macdroid.worknote.features.s05_e03_talking.presentation.TalkRoot
import ru.macdroid.worknote.features.s05_e03_talking.presentation.TalkViewModel
import ru.macdroid.worknote.navigation.graphModels.ChatHost
import ru.macdroid.worknote.navigation.graphModels.TalkHost
import ru.macdroid.worknote.utils.ObserveAsEvents

@Composable
fun TalkHost(onLoginSuccess: () -> Unit) {
    val backStack = remember { mutableStateListOf<TalkHost>(TalkHost.Talk) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<TalkHost.Talk> {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val viewModel: TalkViewModel = koinInject()

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
                TalkRoot(
                    snackbarHostState = snackbarHostState,
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    )
}