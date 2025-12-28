package spbsu.jigokuraku01.front.di

import com.slack.circuit.foundation.Circuit
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import spbsu.jigokuraku01.front.session.SessionRepository
import spbsu.jigokuraku01.front.session.SessionRepositoryTestImpl
import spbsu.jigokuraku01.front.ui.chat.ChatPresenter
import spbsu.jigokuraku01.front.ui.chat.ChatScreen
import spbsu.jigokuraku01.front.ui.chat.ChatUI
import spbsu.jigokuraku01.front.user.UserRepository
import spbsu.jigokuraku01.front.user.UserRepositoryTestImpl

val dataModule = module {
    singleOf<SessionRepository>(::SessionRepositoryTestImpl)
    singleOf<UserRepository>(::UserRepositoryTestImpl)
}

val uiModule = module {
    single {
        Circuit.Builder()
            .addUi< ChatScreen, ChatScreen.State> { state, modifier ->
                ChatUI(state = state, modifier = modifier)
            }
            .addPresenterFactory(factory = ChatPresenter.Factory(get()))
            .build()
    }
}

val testModule = module {
    includes(dataModule, uiModule)
}