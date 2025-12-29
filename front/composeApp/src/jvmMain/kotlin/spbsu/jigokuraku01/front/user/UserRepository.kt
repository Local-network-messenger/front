package spbsu.jigokuraku01.front.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import spbsu.jigokuraku01.front.session.User
import java.util.UUID
import java.util.UUID.randomUUID
import kotlin.collections.emptyList

interface UserRepository {
    fun loadUsers(): Flow<Async<List<User>>>
    fun loadChats() : Flow<Async<List<ChatData>>>
    fun loadChat(chatId: String) : Flow<Async<List<Message>>>
    suspend fun loadNext()
    suspend fun send(message: String)
    suspend fun onLogin()
    suspend fun updateUsers()
}


class UserRepositoryTestImpl : UserRepository {

    private val _chats = MutableStateFlow<Async<List<ChatData>>>(Async.Loading)
    private val _chat = MutableStateFlow<Async<List<Message>>>(Async.Success(emptyList()))
    private val _users = MutableStateFlow<Async<List<User>>>(Async.Loading)


    override fun loadUsers() = _users.onStart { updateUsers() }

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
                dialogId = UUID.randomUUID().toString(),
                userId = UUID.randomUUID().toString(),
                name = "Andrew"
            ),
            ChatData(
                dialogId = UUID.randomUUID().toString(),
                userId = UUID.randomUUID().toString(),
                name = "Arseniy"
            )
        )
        _chats.value = Async.Success(testChats)
    }

    override suspend fun updateUsers() {
        val testUsers = listOf(
            User(uuid = "2", name = "TestUser1"),
            User(uuid = "3", name = "TestUser2")
        )
        _users.value = Async.Success(testUsers)
    }
}

suspend fun <T> runAsync(block : () -> Async<T>) =
    runCatching (block).fold( { it },{ Async.Error(it) } )

