package spbsu.jigokuraku01.front

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import spbsu.jigokuraku01.front.di.testModule
import spbsu.jigokuraku01.front.ui.chat.ChatScreen
import spbsu.jigokuraku01.front.ui.chat.ChatUI
import spbsu.jigokuraku01.front.ui.theme.AppTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "front",
    ) {
        KoinApplication(
            application = {
                modules(testModule)
            }
        ) {
            val circuit: Circuit = koinInject()
            AppTheme {
                CircuitCompositionLocals(circuit) {
                    CircuitContent(ChatScreen)
                }
            }
        }
    }
}