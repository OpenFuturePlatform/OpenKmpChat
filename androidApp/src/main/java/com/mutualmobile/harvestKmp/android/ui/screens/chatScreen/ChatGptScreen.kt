package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.createStore
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.viewmodels.ChatViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.TextType
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.get
import org.koin.androidx.compose.get


val store = CoroutineScope(SupervisorJob()).createStore()
@Composable
fun ChatGptScreen(
    navController: NavHostController,
    crVm: ChatViewModel = get(),
    user: GetUserResponse?,
    userState: PraxisDataModel.DataState
) {

    val scaffoldState = rememberScaffoldState()
    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val loading = crVm.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Assistant") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        }
    ) {
        ChatApp(viewModel =  crVm, modifier = Modifier.padding(it), user = user)
    }

    LaunchedEffect(Unit) {
        //initial fetch messages
        println("INITIAL FETCH ONLY")
        when (userState) {
            is PraxisDataModel.SuccessState<*> -> { crVm.getUserChats(userState = userState) }
            else -> Unit
        }
    }
}

@Composable
fun ChatApp(
    displayTextField: Boolean = true,
    viewModel: ChatViewModel = get(),
    modifier: Modifier,
    user: GetUserResponse?
) {

    val mainActivityViewModel: MainActivityViewModel = get()
    mainActivityViewModel.user
    val myUser = ChatUser(user?.id!!, user.firstName!!, email = user.email!!, picture = null)
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
//                    if (isConnecting) {
//                        Text(
//                            text = "Connecting...", modifier = Modifier
//                                .padding(16.dp)
//                                .fillMaxWidth(), textAlign = TextAlign.Center
//                        )
//                    }
//                    if (!isConnecting) {
//                        Text(
//                            text = "Connected", modifier = Modifier
//                                .padding(16.dp)
//                                .fillMaxWidth(), textAlign = TextAlign.Center
//                        )
//                    }

                    Box(Modifier.weight(1f)) {
                        Messages(messages, myUser)
                    }

                    if (displayTextField) {
                        SendMessage { text, type, _ , _->
                           viewModel.saveChatGptChat(Message(myUser, myUser.name, text, "", type))
                        }
                    }

                }
            }
        }
    }

}