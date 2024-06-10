package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import CustomLinearProgressBar
import SendMessage
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultGroupPicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.ChatPrivateViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.TextType
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import kotlinx.datetime.Clock
import org.koin.androidx.compose.get

@Composable
fun ChatPrivateScreen(
    navController: NavHostController,
    crVm: ChatPrivateViewModel = get(),
    user: GetUserResponse?,
    userState: PraxisDataModel.DataState,
    recipient: String?,
    sender: String?,
    isGroup: String?,
    chatUid: String?
) {
    println("CHAT PRIVATE SCREEN with recipient: $recipient and sender: $sender and chatId: $chatUid")
    println("CanSendMessage : ${crVm.canSendMessage}")
    val scaffoldState = rememberScaffoldState()
    val canSendMessage = crVm.canSendMessage
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    ContactProfileNavigation(
                        isGroup = isGroup == "true",
                        recipient = recipient!!,
                        chatUid = chatUid,
                        onChatClicked = crVm::onContactProfileClicked
                    )
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        scaffoldState = scaffoldState
    ) {
        ChatPrivateApp(
            viewModel = crVm,
            canSendMessage = canSendMessage,
            modifier = Modifier.padding(it),
            user = user,
            recipient = recipient,
            isGroup = isGroup == "true",
            groupChatId = chatUid!!
        )
    }

    LaunchedEffect(crVm.currentNavigationCommand) {
        when (crVm.currentNavigationCommand) {
            is NavigationPraxisCommand -> {
                val destination = (crVm.currentNavigationCommand as NavigationPraxisCommand).screen
                crVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    LaunchedEffect(Unit) {

        when (userState) {
            is PraxisDataModel.SuccessState<*> -> {
                if (chatUid!! == recipient!!) {
                    crVm.getPrivateChats(recipient, sender!!)
                } else {
                    crVm.getPrivateChats(chatUid, isGroup == "true", recipient, sender!!)
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun ContactProfileNavigation(
    isGroup: Boolean,
    recipient: String?,
    chatUid: String?,
    onChatClicked: (String, Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()
        .clickable { onChatClicked.invoke(chatUid!!, isGroup) }
    ) {
        if (isGroup) {
            DefaultGroupPicture(displayName = "$recipient")
        } else {
            DefaultProfilePicture(displayName = "$recipient")
        }
        Text(
            text = "$recipient",
            modifier = Modifier.padding(start = 10.dp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatPrivateApp(
    displayTextField: Boolean = true,
    viewModel: ChatPrivateViewModel = get(),
    canSendMessage: Boolean,
    modifier: Modifier,
    user: GetUserResponse?,
    isGroup: Boolean,
    groupChatId: String,
    recipient: String?
) {

    val mainActivityViewModel: MainActivityViewModel = get()
    mainActivityViewModel.user
    val myUser = ChatUser(user?.id!!, user.firstName!!, email = user.email!!, picture = null)
    val messages = viewModel.chats

    OpenChatTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(painterResource(MR.images.background.drawableResId), null, contentScale = ContentScale.FillHeight)
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.weight(1f)) {
                        Messages(messages, myUser)
                    }
                    if (!canSendMessage) {
                        CustomLinearProgressBar()
                    }
                    if (displayTextField) {
                        SendMessage { text, type, imageByteArray, imageCheckSum ->
                            val receiver = if (isGroup) groupChatId else recipient!!
                            if (type == TextType.ATTACHMENT) {
                                viewModel.uploadAttachment(
                                    imageByteArray!!,
                                    imageCheckSum!!,
                                    Clock.System.now().epochSeconds.toString(),
                                    Message(myUser, recipient = receiver, text, emptyList(), type),
                                    isGroup
                                )
                            }
                            else if (isGroup) {
                                viewModel.saveGroupChat(Message(myUser, recipient = groupChatId, text, emptyList(), type))
                            }
                            else {
                                viewModel.savePrivateChat(Message(myUser, recipient = recipient!!, text, emptyList(), type))
                            }
                        }
                    }
                }
            }
        }
    }
}