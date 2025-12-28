package spbsu.jigokuraku01.front.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import kotlin.collections.emptyList

interface UserRepository {
//    val chats: StateFlow<Async<List<ChatData>>>
    fun loadChats() : Flow<Async<List<ChatData>>>
    fun loadChat(chatId: String) : Flow<Async<List<Message>>>
    suspend fun loadNext()

    suspend fun send(message: String)

    suspend fun onLogin()
}


class UserRepositoryTestImpl : UserRepository {

    private val _chats = MutableStateFlow<Async<List<ChatData>>>(Async.Loading)
    private val _chat = MutableStateFlow<Async<List<Message>>>(Async.Success(emptyList()))

    override fun loadChats() = _chats.onStart { onLogin() }

    override fun loadChat(chatId: String) = _chat.onStart {
        _chat.value = (Async.Success (listOf(Message(uuid = chatId, text = "Hello $chatId"))))
    }

    override suspend fun loadNext() {
        _chat.value = runAsync {
            val prev = _chat.value as Async.Success
            Async.Success(
                List(20) { Message(uuid = it.toString(), "Старое сообщение: $it") } + prev.data
            )
        }
    }

    override suspend fun send(message: String) {
        _chat.value = runAsync {
            val prev = _chat.value as Async.Success
            Async.Success(
                prev.data.plus(Message(
                    uuid = UUID.randomUUID().toString(),
                    text = message
                ))
            )
        }
    }

    override suspend fun onLogin() {
        val testChats = listOf(
            ChatData(
                uuid = "0",
                name = "Andrew"
            ),
            ChatData(
                uuid = "1",
                name = "Arseniy"
            )
        )
        _chats.value = Async.Success(testChats)
    }
}

suspend fun <T> runAsync(block : () -> Async<T>) =
    runCatching (block).fold( { it },{ Async.Error(it) } )

