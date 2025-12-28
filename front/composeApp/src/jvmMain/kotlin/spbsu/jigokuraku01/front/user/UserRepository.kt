package spbsu.jigokuraku01.front.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlin.collections.emptyList

interface UserRepository {
//    val chats: StateFlow<Async<List<ChatData>>>
    fun loadChats() : Flow<Async<List<ChatData>>>
    fun loadChat(chatId: Int) : Flow<Async<List<Message>>>
    suspend fun loadNext()

    suspend fun send(message: Message)

    suspend fun onLogin()
}


class UserRepositoryTestImpl : UserRepository {

    private val _chats = MutableStateFlow<Async<List<ChatData>>>(Async.Loading)
    private val _chat = MutableStateFlow<Async<List<Message>>>(Async.Success(emptyList()))

    override fun loadChats() = _chats.onStart { onLogin() }

    override fun loadChat(chatId: Int) = _chat.onStart {
        _chat.value = (Async.Success (listOf(Message(id = chatId, text = "Hello $chatId"))))
    }

    override suspend fun loadNext() {
        _chat.value = runAsync {
            val prev = _chat.value as Async.Success
            Async.Success(
                List(20) { Message(id = it, "Старое сообщение: $it") } + prev.data
            )
        }
    }

    override suspend fun send(message: Message) {
        _chat.value = runAsync {
            val prev = _chat.value as Async.Success
            Async.Success(
                prev.data.plus(message)
            )
        }
    }

    override suspend fun onLogin() {
        val testChats = listOf(
            ChatData(
                id = 0,
                name = "Andrew"
            ),
            ChatData(
                id = 1,
                name = "Arseniy"
            )
        )
        _chats.value = Async.Success(testChats)
    }
}

suspend fun <T> runAsync(block : () -> Async<T>) =
    runCatching (block).fold( { it },{ Async.Error(it) } )

