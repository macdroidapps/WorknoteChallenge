package ru.macdroid.worknote.features.s05_e01_chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatEvent
import ru.macdroid.worknote.features.s05_e01_chat.domain.ChatState
import ru.macdroid.worknote.features.s05_e01_chat.domain.models.MessageModel

@Composable
fun ChatRoot(
    state: ChatState,
    onEvent: (ChatEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (!state.userName.isNullOrEmpty()) {
                MessageInputBar(
                    currentMessage = state.currentMessage.orEmpty(),
                    onMessageChange = { onEvent(ChatEvent.UpdateCurrentMessage(it)) },
                    onSendClick = {
                        onEvent(ChatEvent.SendMessageToChat(state.currentMessage.orEmpty()))
                    }
                )
            }
        }
    ) { paddingValues ->
        when (state.userName.isNullOrEmpty()) {
            true -> Auth(
                onEvent = onEvent
            )
            false -> ChatScreen(
                state = state,
                onEvent = onEvent,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun Auth(
    onEvent: (ChatEvent) -> Unit,
) {
    var userName by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Введите ваше имя") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onEvent(ChatEvent.SetUserName(userName)) },
            enabled = userName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Начать чат")
        }
    }
}

@Composable
fun ChatScreen(
    state: ChatState,
    onEvent: (ChatEvent) -> Unit,
    paddingValues: PaddingValues
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.chatMessages.size) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        ChatHeader(title = "Чат ${state.userName} с Claude")

        if (state.chatMessages.isEmpty()) {
            EmptyStateView()
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.chatMessages) { _, message->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

@Composable
fun ChatHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "👋",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Начните разговор с Claude",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MessageBubble(message: MessageModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.role == "user") Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.role == "user") 16.dp else 4.dp,
                bottomEnd = if (message.role == "user") 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.role == "user")
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.role == "user")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun MessageInputBar(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = currentMessage,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Напишите сообщение...") },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp),
            maxLines = 5
        )

        Button(
            onClick = onSendClick,
            enabled = currentMessage.isNotBlank()
        ) {
            Text("Отправить")
        }
    }
}