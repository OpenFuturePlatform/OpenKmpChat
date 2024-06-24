package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import CustomLinearProgressBar
import SendMessage
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.screens.common.AssistantDatePickerDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.CommonAlertDialog
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultGroupPicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.formatter
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.serverDateFormatter
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.ChatPrivateViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.TextType
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import kotlinx.datetime.Clock
import org.koin.androidx.compose.get
import java.util.*
import kotlin.streams.toList

@RequiresApi(Build.VERSION_CODES.N)
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
    val context = LocalContext.current
    println("CHAT PRIVATE SCREEN with recipient: $recipient and sender: $sender and chatId: $chatUid")
    println("CanSendMessage : ${crVm.canSendMessage}")
    val scaffoldState = rememberScaffoldState()
    val canSendMessage = crVm.canSendMessage

    var menuExpanded by remember {
        mutableStateOf(false)
    }

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
                actions = {

                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(

                            content = {
                                Text("Get Notes")
                            },
                            onClick = {
                                crVm.getAssistantNotes(chatUid!!, isGroup == "true")
                            },
                        )
                        DropdownMenuItem(

                            content = {
                                Text("Take Notes")
                            },
                            onClick = {
                                //crVm.addAssistantNotes(chatUid!!, isGroup == "true")
                                crVm.isAssistantConfirmDialogVisible = true
                                crVm.assistantConfirmChatId = chatUid!!
                                crVm.assistantConfirmIsGroup = isGroup!!
                            },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Take Reminders")
                            },
                            onClick = { crVm.addAssistantReminders(chatUid!!, isGroup == "true") },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Take ToDos")
                            },
                            onClick = { crVm.addAssistantToDos(chatUid!!, isGroup == "true") },
                        )
                    }
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

    if (crVm.isAssistantConfirmDialogVisible) {
        println("ADD NOTES FOR ${crVm.assistantConfirmChatId}")

        AssistantDatePickerDialog(
            onDismiss = {
                crVm.isAssistantConfirmDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
            },
            onConfirm = {
                crVm.addAssistantNotes()
                crVm.isAssistantConfirmDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
                Toast.makeText(
                    context,
                    "STARTED GETTING NOTES",
                    Toast.LENGTH_SHORT
                ).show()
            },
            titleProvider = { MR.strings.assistant_datepicker_title.get() },
            startDateProvider = {
                crVm.assistantStartDate = serverDateFormatter.format(it)
            },
            endDateProvider = {
                crVm.assistantEndDate = serverDateFormatter.format(it)
            }

        )
    }

    if (crVm.assistantNotesReady) {

        CommonAlertDialog(
            onDismiss = {
                crVm.assistantNotesReady = false
                crVm.assistantNotes = emptyList()
            },
            onConfirm = {
                crVm.assistantNotesReady = false
                crVm.assistantNotes = emptyList()
            },
            titleProvider = { MR.strings.assistant_notes.get() },
            bodyTextProvider = {  crVm.assistantNotes.map { a -> a.notes }.toString() }
        )

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
                            } else if (isGroup) {
                                viewModel.saveGroupChat(
                                    Message(
                                        myUser,
                                        recipient = groupChatId,
                                        text,
                                        emptyList(),
                                        type
                                    )
                                )
                            } else {
                                viewModel.savePrivateChat(
                                    Message(
                                        myUser,
                                        recipient = recipient!!,
                                        text,
                                        emptyList(),
                                        type
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}