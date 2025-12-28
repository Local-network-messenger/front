package spbsu.jigokuraku01.front.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import spbsu.jigokuraku01.front.user.ChatData
import spbsu.jigokuraku01.front.user.Message
import spbsu.jigokuraku01.front.user.UserRepository
import spbsu.jigokuraku01.front.user.Async

sealed interface ChatEvent {
    data class SelectChat(val chatId: Int) : ChatEvent
}
data object ChatScreen : Screen {
    data class State(
        val chats: Async<List<ChatData>>,
        val chosenChat: Int?,
        val messages: Async<List<Message>>,
        val eventSink: (ChatEvent) -> Unit
    ) : CircuitUiState
}

class ChatPresenter(
    private val userRepository: UserRepository
) : Presenter<ChatScreen.State> {
    @Composable
    override fun present(): ChatScreen.State {
        var chosenChat by remember { mutableStateOf<Int?>(null) }

        val chatsFlow = remember {
            userRepository.loadChats()
        }

        val chatFlow = remember(chosenChat) {
            chosenChat?.let { userRepository.loadChat(it) }
        }

        val chats by chatsFlow.collectAsStateWithLifecycle(initialValue = Async.Loading)
        val chat by chatFlow?.collectAsStateWithLifecycle(initialValue = Async.Loading)
            ?: remember { mutableStateOf<Async<List<Message>>>(Async.Loading) }

        return ChatScreen.State(
            chats = chats,
            chosenChat = chosenChat,
            messages = chat,
            eventSink = { event ->
                when (event) {
                    is ChatEvent.SelectChat -> chosenChat = event.chatId
                }
            }
        )
    }
}
