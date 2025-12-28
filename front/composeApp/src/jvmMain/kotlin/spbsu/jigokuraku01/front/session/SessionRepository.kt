package spbsu.jigokuraku01.front.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import spbsu.jigokuraku01.front.session.Session.LoggedOut
import spbsu.jigokuraku01.front.session.User

sealed interface Session {
    data class LoggedIn(val user: User) : Session
    object LoggedOut : Session
}


/**
 * [SessionRepository] - хранит состояние сессии
 *
 * Lifecycle:
 * - Singleton, application scope
 * - DI контейнер
 */
interface SessionRepository {
    val session: Flow<Session>
    suspend fun login(username: String): Boolean
    suspend fun logout()
}

class SessionRepositoryTestImpl : SessionRepository {
    private val _session = MutableStateFlow<Session>(LoggedOut)
    override val session: Flow<Session> = _session

    override suspend fun logout () {
        _session.value = LoggedOut
    }

    override suspend fun login(username: String): Boolean {
        _session.value = Session.LoggedIn(User(uuid = username, name = username))
        return true
    }
}