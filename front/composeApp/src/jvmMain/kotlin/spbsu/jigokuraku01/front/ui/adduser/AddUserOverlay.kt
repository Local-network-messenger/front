package spbsu.jigokuraku01.front.ui.adduser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.overlay.Overlay
import com.slack.circuit.overlay.OverlayNavigator
import kotlinx.coroutines.flow.Flow
import spbsu.jigokuraku01.front.session.User
import spbsu.jigokuraku01.front.ui.chat.Chat
import spbsu.jigokuraku01.front.ui.chat.ChatEvent
import spbsu.jigokuraku01.front.user.Async
import spbsu.jigokuraku01.front.user.ChatData

data class AddUserOverlay(
    private val userList: Flow<Async<List<User>>>,
    private val onClick: (User) -> Unit
) : Overlay<Unit> {

    @Composable
    override fun Content(navigator: OverlayNavigator<Unit>) {
        val usersFlow = remember {
            userList
        }
        val users by usersFlow.collectAsStateWithLifecycle(initialValue = Async.Loading)

        when (users) {
            is Async.Success -> {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it }
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it }
                    )
                ) {
                    Surface(modifier = Modifier.fillMaxWidth(0.3f).fillMaxHeight()) {
                        ChatColumn((users as Async.Success<List<User>>).data, onClick, {
                            navigator.finish(Unit)
                        })
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun ChatColumn(users: List<User>, onClick: (User) -> Unit, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val state = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    PullToRefreshBox(
        onRefresh = {},
        isRefreshing = isRefreshing,
        state = state,
    ) {
        LazyColumn(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
            item {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp).height(30.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.weight(0.15f).scale(1f),
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                            )
                    }
                    Text(
                        modifier = Modifier.weight(0.8f),
                        text = "Пользователи", style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left
                    )
                }
            }
            items(items = users) { chatData ->
                ChatRow(chatData, onClick)
            }
        }
    }
}


@Composable
private fun ChatRow(user: User, onClick: (User) -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.padding(0.dp).fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        onClick = { onClick(user) }
    ) {
        Text(user.name, style = MaterialTheme.typography.labelLarge)
    }
}