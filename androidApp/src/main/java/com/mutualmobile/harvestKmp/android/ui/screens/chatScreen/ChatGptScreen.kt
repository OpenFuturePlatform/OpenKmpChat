package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import CustomLinearProgressBar
import DotsPulsing
import SendMessage
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.viewmodels.ChatViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
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
    userState: PraxisDataModel.DataState
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Assistant") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        }
    ) {  scaffoldPadding -> ChatApp(viewModel =  crVm,  user = user, paddingValues = scaffoldPadding)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatApp(
    displayTextField: Boolean = true,
    viewModel: ChatViewModel = get(),
    user: GetUserResponse?,
    paddingValues: PaddingValues
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