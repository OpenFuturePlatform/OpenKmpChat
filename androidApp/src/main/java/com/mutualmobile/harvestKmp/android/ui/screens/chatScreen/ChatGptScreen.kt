package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import DotsPulsing
import SendMessage
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.viewmodels.ChatViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import org.koin.androidx.compose.get


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatGptScreen(
    navController: NavHostController,
    crVm: ChatViewModel = get(),
    user: GetUserResponse?,
    userState: OpenDataModel.DataState
) {

    // 1
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Assistant") },
                backgroundColor = MaterialTheme.colors.primary,
                actions = {
                    // 3
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                        )
                    }
                    // 4
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                        )
                    }
                    // 5
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        // 6
                        DropdownMenuItem(

                            content = {
                                Text("Refresh")
                            },
                            onClick = { /* TODO */ },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Settings")
                            },
                            onClick = { /* TODO */ },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("About")
                            },
                            onClick = { /* TODO */ },
                        )
                    }
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        }
    ) {  scaffoldPadding -> ChatApp(viewModel =  crVm, user = user)
    }

    LaunchedEffect(Unit) {
        //initial fetch messages
        println("INITIAL FETCH ONLY")
        when (userState) {
            is OpenDataModel.SuccessState<*> -> { crVm.getUserChats(userState = userState) }
            else -> Unit
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatApp(
    displayTextField: Boolean = true,
    viewModel: ChatViewModel = get(),
    user: GetUserResponse?
) {

    val mainActivityViewModel: MainActivityViewModel = get()
    mainActivityViewModel.user
    val myUser = ChatUser(user?.id!!, user.firstName!!, email = user.email!!, picture = null)
    val messages = viewModel.chats
    val canSendMessage = viewModel.canSendMessage

    println("Can send message: $canSendMessage")

    OpenChatTheme {
        Surface {
            Box(modifier = Modifier
                .fillMaxSize()
            ) {
                Image(painterResource(MR.images.background.drawableResId), null, contentScale = ContentScale.FillHeight)
                Column(
                    modifier = Modifier
                        .imePadding()
                        .wrapContentHeight()
                        .fillMaxHeight()
                        .fillMaxSize()
                ) {

                    Box(Modifier.weight(1f)) {
                        Messages(messages, myUser)
                    }

                    if (!canSendMessage){
                        //CustomLinearProgressBar()
                        DotsPulsing()
                    }
                    if (displayTextField) {
                        SendMessage { text, type, _ , _->
                           viewModel.saveChatGptChat(Message(myUser, myUser.name, text, emptyList(), type))
                        }
                    }

                }
            }
        }
    }

}