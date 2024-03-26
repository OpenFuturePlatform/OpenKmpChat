package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import SendMessage
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Action
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.viewmodels.ChatRoomViewModel
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.createStore
import com.mutualmobile.harvestKmp.android.viewmodels.ChatViewModel
import com.mutualmobile.harvestKmp.db.flattenToList
import com.mutualmobile.harvestKmp.di.SharedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.get


//val myUser = ChatUser("Me", picture = null)
//val friends = listOf(
//    ChatUser("Alex", picture = MR.images.stock2),
//    ChatUser("Casey", picture = MR.images.stock1),
//    ChatUser("Sam", picture = MR.images.stock3)
//)
val friendMessages = listOf(
    "How's everybody doing today?",
    "I've been meaning to chat!",
    "When do we hang out next? 😋",
    "We really need to catch up!",
    "It's been too long!",
    "I can't\nbelieve\nit! 😱",
    "Did you see that ludicrous\ndisplay last night?",
    "We should meet up in person!",
    "How about a round of pinball?",
    "I'd love to:\n🍔 Eat something\n🎥 Watch a movie, maybe?\nWDYT?"
)

val store = CoroutineScope(SupervisorJob()).createStore()
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatGptScreen(
    navController: NavHostController,
    crVm: ChatViewModel = get(),
    user: GetUserResponse?
) {

    val scaffoldState = rememberScaffoldState()
    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val loading = crVm.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open Chat") },
                backgroundColor = MaterialTheme.colors.primary,
            )
        }
    ) {
        ChatApp(viewModel =  crVm, modifier = Modifier.padding(it), user = user)
    }
}

@Composable
fun ChatApp(
    displayTextField: Boolean = true,
    viewModel: ChatViewModel = get(),
    modifier: Modifier,
    user: GetUserResponse?
) {
//    val state by store.stateFlow.collectAsState()
//    val messages = state.messages

    val myUser = ChatUser(user?.id!!, user.firstName!!, picture = null)

    val state by viewModel.chats.collectAsState()
    val messages = state.map { Message(myUser, it.content!!, it.time!!, it.uid.toLong()) }

    OpenChatTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(painterResource(MR.images.background.drawableResId), null, contentScale = ContentScale.Crop)
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.weight(1f)) {
                        Messages(messages, myUser)
                    }
                    if (displayTextField) {
                        SendMessage { text ->
//                            store.send(
//                                Action.SendMessage(
//                                    Message(myUser, text)
//                                )
//                            )
                           viewModel.saveChat(Message(myUser, text))
                        }
                    }
                }
            }
        }
    }
//    LaunchedEffect(Unit) {
//        var lastFriend = friends.random()
//        var lastMessage = friendMessages.random()
//        while (true) {
//            val thisFriend = friends.random()
//            val thisMessage = friendMessages.random()
//            if(thisFriend == lastFriend) continue
//            if(thisMessage == lastMessage) continue
//            lastFriend = thisFriend
//            lastMessage = thisMessage
//            store.send(
//                com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Action.SendMessage(
//                    message = com.mutualmobile.harvestKmp.domain.model.Message(
//                        user = thisFriend,
//                        text = thisMessage
//                    )
//                )
//            )
//            delay(50000)
//        }
//    }
}