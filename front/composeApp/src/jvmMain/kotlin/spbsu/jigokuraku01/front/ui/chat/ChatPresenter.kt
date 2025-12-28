package spbsu.jigokuraku01.front.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.launch
import spbsu.jigokuraku01.front.user.ChatData
import spbsu.jigokuraku01.front.user.Message
import spbsu.jigokuraku01.front.user.UserRepository
import spbsu.jigokuraku01.front.user.Async
import kotlin.collections.getValue
import kotlin.collections.setValue

sealed interface ChatEvent {
    data class SelectChat(val chatId: String) : ChatEvent
    data class ChangeMessage(val message: String) : ChatEvent
    object SendMessage : ChatEvent
}
data object ChatScreen : Screen {
    data class State(
        val chats: Async<List<ChatData>>,
        val chosenChat: String?,
        val messages: Async<List<Message>>,
        val sendMessage: String,
        val eventSink: (ChatEvent) -> Unit
    ) : CircuitUiState
}

class ChatPresenter(
    private val userRepository: UserRepository
) : Presenter<ChatScreen.State> {
    @Composable
    override fun present(): ChatScreen.State {
        val scope = rememberCoroutineScope()
        var chosenChat by remember { mutableStateOf<String?>(null) }
        var sendMessage by remember { mutableStateOf("") }
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
            sendMessage = sendMessage,
            eventSink = { event ->
                when (event) {
                    is ChatEvent.SelectChat -> chosenChat = event.chatId
                    is ChatEvent.ChangeMessage -> sendMessage = event.message
                    is ChatEvent.SendMessage -> scope.launch { userRepository.send(sendMessage) }
                }
            }
        )
    }

    class Factory(
        private val userRepository: UserRepository
    ) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is ChatScreen -> ChatPresenter(userRepository = userRepository)
                else -> null
            }
        }
    }
}
