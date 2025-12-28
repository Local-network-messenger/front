package spbsu.jigokuraku01.front.ui.chat

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import spbsu.jigokuraku01.front.ui.theme.AppTheme
import spbsu.jigokuraku01.front.user.Async
import spbsu.jigokuraku01.front.user.ChatData
import spbsu.jigokuraku01.front.user.Message

@Composable
fun ChatUI(state: ChatScreen.State, modifier: Modifier = Modifier) {
    Surface() {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            when (state.chats) {
                is Async.Success -> {
                    ChatColumn(state.chats.data, state.eventSink, modifier.weight(0.3f))
                }
                else -> {}
            }
            when (state.chosenChat) {
                null -> EmptyChat(Modifier.weight(0.7f))
                else -> {
                    when (state.messages) {
                        is Async.Success -> {
                            Chat(state.messages.data, state.sendMessage, state.eventSink, Modifier.weight(0.7f))
                        }
                        else -> LoadingChat(Modifier.weight(0.7f))
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingChat(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
    ) {
    }
}

@Composable
fun EmptyChat(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = "Выберите чат")
        }
    }
}

@Composable
fun Chat(messages: List<Message>, sendMessage: String, eventSink: (ChatEvent) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
    ) {
        Column {
            MessageColumn(messages, Modifier.weight(0.85f))
            TextField(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp).weight(0.15f),
                value = sendMessage,
                shape = MaterialTheme.shapes.medium,
                placeholder = { Text("Введите сообщение") },
                onValueChange = { eventSink(ChatEvent.ChangeMessage(it)) },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                    focusedContainerColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.surface,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.surface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        }
    }
}

@Composable
fun MessageColumn(messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),) {
        items(items = messages) { message ->
            MessageRow(message, modifier)
        }
    }
}

@Composable
fun MessageRow(message: Message, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(30.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,)
    }
}

@Composable
fun ChatColumn(chats: List<ChatData>, eventSink: (ChatEvent) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        item {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "Чаты", style = MaterialTheme.typography.titleLarge)
        }
        items(items = chats) { chatData ->
            ChatRow(chatData, eventSink)
        }
    }
}

@Composable
fun ChatRow(chat: ChatData, eventSink: (ChatEvent) -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.padding(0.dp).fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        onClick = { eventSink(ChatEvent.SelectChat(chat.uuid)) }
    ) {
        Column(modifier = modifier.padding(0.dp).fillMaxWidth()) {
            Text(chat.name, style = MaterialTheme.typography.labelLarge)
            Text(
                chat.name,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    AppTheme {
        ChatUI(
            ChatScreen.State(
                chats = Async.Success(listOf(
                    ChatData(uuid = "1", name = "Andrew"),
                    ChatData(uuid = "2", name = "Arseniy")
                )),
                chosenChat = null,
                messages = Async.Success(listOf(
                    Message(uuid = "0", text = "Hello World"),
                    Message(uuid = "1", text = "Hello World Hello World")
                )),
                sendMessage = "",
                eventSink = {}
            )
        )
    }
}