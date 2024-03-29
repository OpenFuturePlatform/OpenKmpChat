package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import ChatNewBox
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import SendMessage
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.mutualmobile.harvestKmp.domain.model.MessagesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.get


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
        },
//        bottomBar = {
//            SendMessage { text ->
//                crVm.saveChat(Message(ChatUser(user?.id!!, user.firstName!!, picture = null), text))
//            }
//        }
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

    val myUser = ChatUser(user?.id!!, user.firstName!!, picture = null)
    val messages = viewModel.chats
    val state by viewModel.state.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val showConnectionError by viewModel.showConnectionError.collectAsState()


    OpenChatTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(painterResource(MR.images.background.drawableResId), null, contentScale = ContentScale.FillHeight)
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isConnecting) {
                        Text(
                            text = "Connecting...", modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(), textAlign = TextAlign.Center
                        )
                    }

                    if (!isConnecting) {
                        Text(
                            text = "Connected", modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(), textAlign = TextAlign.Center
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        Messages(messages, myUser)
                    }
                    if (displayTextField) {
                        SendMessage { text ->
                           viewModel.saveChat(Message(myUser, text))
                        }
//                        ChatNewBox { text ->
//                            viewModel.saveChat(Message(myUser, text))
//                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        //initial fetch messages
        println("INITIAL FETCH ONLY")
        //viewModel.getUserChats(user.email!!)
    }
}